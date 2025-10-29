package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when an external service call fails.
 * 
 * <p>This exception is raised when communication with external services
 * (like OmniDocs REST APIs) encounters errors.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 502 (Bad Gateway) or HTTP 503 (Service Unavailable) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>External service timeout</li>
 *   <li>Network errors</li>
 *   <li>Service returns error response</li>
 *   <li>Unexpected response format</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#EXTERNAL_SERVICE_ERROR
 */
public class ExternalServiceException extends BaseException {

    /**
     * Constructs a new ExternalServiceException with the specified details.
     * 
     * @param details descriptive message about the service failure
     */
    public ExternalServiceException(String details) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR, details);
    }

    /**
     * Constructs a new ExternalServiceException with details and cause.
     * 
     * @param details descriptive message about the service failure
     * @param cause the underlying cause of the exception
     */
    public ExternalServiceException(String details, Throwable cause) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR, details, cause);
    }
}
