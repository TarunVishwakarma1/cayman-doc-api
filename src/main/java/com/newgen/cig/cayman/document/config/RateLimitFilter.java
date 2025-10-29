package com.newgen.cig.cayman.document.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
import com.newgen.cig.cayman.document.exception.TooManyRequestsException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    @Value("${rate.limit.capacity:100}")
    private int rateLimitCapacity;
    
    @Value("${rate.limit.duration.minutes:1}")
    private int rateLimitDurationMinutes;
    
    // Cache with automatic eviction after 10 minutes of inactivity
    private final Cache<String, TokenBucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    private TokenBucket createNewBucket() {
        return new TokenBucket(rateLimitCapacity, rateLimitDurationMinutes);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RateLimitFilter initialized");
        logger.info("Rate limit configuration - Capacity: {} requests, Duration: {} minute(s)", 
                rateLimitCapacity, rateLimitDurationMinutes);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ip = getClientIP(httpRequest);
        
        logger.trace("Rate limit check for IP: {}", ip);
        
        TokenBucket bucket = cache.get(ip, k -> createNewBucket());
        
        if (bucket.tryConsume()) {
            logger.trace("Request allowed for IP: {}", ip);
            chain.doFilter(request, response);
        } else {
            logger.warn("Rate limit exceeded for IP: {}", ip);
            throw new TooManyRequestsException("Rate limit exceeded. Please try again later");
        }
    }
    
    @Override
    public void destroy() {
        logger.info("RateLimitFilter destroyed, cleaning up cache");
        cache.invalidateAll();
    }

    /**
     * Simple Token Bucket implementation for rate limiting
     */
    private static class TokenBucket {
        private final int capacity;
        private final long refillIntervalNanos;
        private final long tokensPerRefill;
        private long availableTokens;
        private long lastRefillTimestamp;

        public TokenBucket(int capacity, int durationMinutes) {
            this.capacity = capacity;
            this.availableTokens = capacity;
            this.lastRefillTimestamp = System.nanoTime();
            this.refillIntervalNanos = TimeUnit.MINUTES.toNanos(durationMinutes);
            this.tokensPerRefill = capacity;
        }

        public synchronized boolean tryConsume() {
            refill();
            if (availableTokens > 0) {
                availableTokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            long timePassed = now - lastRefillTimestamp;
            
            if (timePassed >= refillIntervalNanos) {
                long refillCycles = timePassed / refillIntervalNanos;
                long tokensToAdd = refillCycles * tokensPerRefill;
                availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
                lastRefillTimestamp = now - (timePassed % refillIntervalNanos);
            }
        }
    }
}
