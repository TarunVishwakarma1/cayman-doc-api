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

/**
 * Servlet filter that implements rate limiting using the Token Bucket algorithm.
 * 
 * <p>This filter protects the API from abuse by limiting the number of requests
 * that can be made from a single IP address within a specified time window.</p>
 * 
 * <h3>Algorithm:</h3>
 * <p>Uses the Token Bucket algorithm where:</p>
 * <ul>
 *   <li>Each IP address gets a bucket with a fixed capacity of tokens</li>
 *   <li>Each request consumes one token</li>
 *   <li>Tokens refill at a fixed rate (capacity per duration)</li>
 *   <li>Requests are rejected when the bucket is empty</li>
 * </ul>
 * 
 * <h3>Configuration:</h3>
 * <pre>
 * # application.yml
 * rate:
 *   limit:
 *     capacity: 100           # Maximum requests per time window
 *     duration:
 *       minutes: 1            # Time window duration
 * </pre>
 * 
 * <h3>Features:</h3>
 * <ul>
 *   <li>Per-IP address rate limiting</li>
 *   <li>Support for X-Forwarded-For header (proxy-aware)</li>
 *   <li>Automatic cache eviction after 10 minutes of inactivity</li>
 *   <li>Maximum 10,000 IP addresses tracked simultaneously</li>
 * </ul>
 * 
 * <h3>HTTP Status:</h3>
 * <p>When rate limit is exceeded, throws {@link TooManyRequestsException}
 * which results in HTTP 429 (Too Many Requests) response.</p>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see TooManyRequestsException
 * @see TokenBucket
 */
@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    /** Maximum number of requests allowed per time window (configurable) */
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
     * Simple Token Bucket implementation for rate limiting.
     * 
     * <p>The Token Bucket algorithm allows for burst traffic while maintaining
     * an average rate limit over time.</p>
     * 
     * <h3>How It Works:</h3>
     * <ul>
     *   <li>Bucket starts with maximum capacity of tokens</li>
     *   <li>Each request consumes one token</li>
     *   <li>Tokens refill automatically based on time passed</li>
     *   <li>Bucket capacity is never exceeded</li>
     * </ul>
     * 
     * <p>This implementation is thread-safe using synchronized methods.</p>
     * 
     * @author Tarun Vishwakarma
     * @version 1.0
     * @since 2025
     */
    private static class TokenBucket {
        /** Maximum number of tokens the bucket can hold */
        private final int capacity;
        
        /** Time interval (in nanoseconds) for refilling tokens */
        private final long refillIntervalNanos;
        
        /** Number of tokens added during each refill cycle */
        private final long tokensPerRefill;
        
        /** Current number of available tokens */
        private long availableTokens;
        
        /** Timestamp of the last refill operation */
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
