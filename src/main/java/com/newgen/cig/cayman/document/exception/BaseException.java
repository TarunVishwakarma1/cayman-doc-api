package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base exception class for all custom exceptions in the Cayman Document API.
 *
 * <p>This abstract exception provides a consistent structure for error handling across
 * the application by associating an error code with each exception instance.</p>
 *
 * <h3>Purpose:</h3>
 * <ul>
 *   <li>Provides standardized error handling across the application</li>
 *   <li>Associates error codes with exceptions for easier client-side handling</li>
 *   <li>Serves as the parent class for all domain-specific exceptions</li>
 *   <li>Enables consistent error response formatting</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * public class CustomException extends BaseException {
 *     public CustomException(String details) {
 *         super(ErrorCode.CUSTOM_ERROR, details);
 *     }
 * }
 * }</pre>
 *
 * <h3>Error Response Structure:</h3>
 * <p>When caught by the global exception handler, BaseException instances are
 * converted to structured error responses with HTTP status codes, error codes,
 * and descriptive messages.</p>
 *
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode
 * @see GlobalExceptionHandler
 */
public abstract class BaseException extends RuntimeException {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseException.class);
    /** The error code associated with this exception */
    private final ErrorCode errorCode;
    /** The details associated with this exception */
    private final String details;

    /**
     * Constructs a new BaseException with the specified error code and details.
     *
     * @param errorCode the error code identifying the type of error
     * @param details descriptive message about the error
     */
    protected BaseException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage() + (details != null ? ": " + details : ""));
        this.errorCode = errorCode;
        this.details = details;
        logger.error("{} created - Code: {}, Message: {}, Details: {}", 
                this.getClass().getSimpleName(), errorCode.getCode(), errorCode.getMessage(), details);
    }

    /**
     * Constructs a new BaseException with error code, details, and cause.
     *
     * @param errorCode the error code identifying the type of error
     * @param details descriptive message about the error
     * @param cause the underlying cause of the exception
     */
    protected BaseException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage() + (details != null ? ": " + details : ""), cause);
        this.errorCode = errorCode;
        this.details = details;
        logger.error("{} created - Code: {}, Message: {}, Details: {}", 
                this.getClass().getSimpleName(), errorCode.getCode(), errorCode.getMessage(), details, cause);
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the ErrorCode enum value
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the String details associated with this exception.
     *
     * @return the details String value
     */
    public String getDetails() {
        return details;
    }
}
