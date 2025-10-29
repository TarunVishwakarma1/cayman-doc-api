
package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when a required parameter is missing from an API request.
 * 
 * <p>This exception is raised when mandatory request parameters, headers,
 * or body fields are not provided.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 400 (Bad Request) response</p>
 * 
 * <h3>Common Scenarios:</h3>
 * <ul>
 *   <li>Required path parameters not provided</li>
 *   <li>Missing request body fields</li>
 *   <li>Absent required headers</li>
 *   <li>Null values for mandatory parameters</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#MISSING_PARAMETER
 */
public class MissingParameterException extends BaseException {

    /**
     * Constructs a new MissingParameterException with the specified details.
     * 
     * @param details descriptive message about the missing parameter
     */
    public MissingParameterException(String details) {
        super(ErrorCode.MISSING_PARAMETER, details);
    }

    /**
     * Constructs a new MissingParameterException with details and cause.
     * 
     * @param details descriptive message about the missing parameter
     * @param cause the underlying cause of the exception
     */
    public MissingParameterException(String details, Throwable cause) {
        super(ErrorCode.MISSING_PARAMETER, details, cause);
    }
}
