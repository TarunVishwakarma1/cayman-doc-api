package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when an invalid parameter value is provided to an API endpoint.
 * 
 * <p>This exception is raised when request parameters fail validation rules,
 * such as format constraints, value ranges, or business logic requirements.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 400 (Bad Request) response</p>
 * 
 * <h3>Common Scenarios:</h3>
 * <ul>
 *   <li>Empty or null values for required parameters</li>
 *   <li>Invalid format (e.g., invalid base64 encoding)</li>
 *   <li>Out-of-range values</li>
 *   <li>Malformed data structures</li>
 *   <li>Invalid enum values</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#INVALID_PARAMETER
 */
public class InvalidParameterException extends BaseException {

    /**
     * Constructs a new InvalidParameterException with the specified details.
     * 
     * @param details descriptive message about the invalid parameter
     */
    public InvalidParameterException(String details) {
        super(ErrorCode.INVALID_PARAMETER, details);
    }

    /**
     * Constructs a new InvalidParameterException with details and cause.
     * 
     * @param details descriptive message about the invalid parameter
     * @param cause the underlying cause of the exception
     */
    public InvalidParameterException(String details, Throwable cause) {
        super(ErrorCode.INVALID_PARAMETER, details, cause);
    }
}
