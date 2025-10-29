package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when rate limiting thresholds are exceeded.
 * 
 * <p>This exception is raised by the {@link com.newgen.cig.cayman.document.config.RateLimitFilter}
 * when a client exceeds the configured number of requests within the specified time window.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 429 (Too Many Requests) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>Client making too many requests in a short period</li>
 *   <li>Automated scripts without rate limiting</li>
 *   <li>Potential denial-of-service attempts</li>
 * </ul>
 * 
 * <h3>Client Handling:</h3>
 * <pre>{@code
 * // Recommended client retry logic
 * try {
 *     response = apiClient.call();
 * } catch (TooManyRequestsException e) {
 *     Thread.sleep(60000); // Wait 1 minute
 *     response = apiClient.call(); // Retry
 * }
 * }</pre>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see com.newgen.cig.cayman.document.config.RateLimitFilter
 * @see ErrorCode#TOO_MANY_REQUESTS
 */
public class TooManyRequestsException extends BaseException {

    /**
     * Constructs a new TooManyRequestsException with the specified details.
     * 
     * @param details descriptive message about the rate limit violation
     */
    public TooManyRequestsException(String details) {
        super(ErrorCode.TOO_MANY_REQUESTS, details);
    }

    /**
     * Constructs a new TooManyRequestsException with details and cause.
     * 
     * @param details descriptive message about the rate limit violation
     * @param cause the underlying cause of the exception
     */
    public TooManyRequestsException(String details, Throwable cause) {
        super(ErrorCode.TOO_MANY_REQUESTS, details, cause);
    }
}
