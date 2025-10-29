package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when a cabinet session has expired.
 * 
 * <p>This exception is raised when attempting to perform operations with
 * an expired or invalid session ID.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 401 (Unauthorized) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>Session timeout due to inactivity</li>
 *   <li>Invalid or corrupted session ID</li>
 *   <li>Server restart causing session loss</li>
 * </ul>
 * 
 * <h3>Resolution:</h3>
 * <p>Client should obtain a new session ID and retry the operation</p>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#SESSION_EXPIRED
 */
public class SessionExpiredException extends BaseException {

    /**
     * Constructs a new SessionExpiredException with the specified details.
     * 
     * @param details descriptive message about the session expiration
     */
    public SessionExpiredException(String details) {
        super(ErrorCode.SESSION_EXPIRED, details);
    }

    /**
     * Constructs a new SessionExpiredException with details and cause.
     * 
     * @param details descriptive message about the session expiration
     * @param cause the underlying cause of the exception
     */
    public SessionExpiredException(String details, Throwable cause) {
        super(ErrorCode.SESSION_EXPIRED, details, cause);
    }
}
