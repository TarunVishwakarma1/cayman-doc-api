package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when JSON parsing operations fail.
 * 
 * <p>This exception is raised when the application cannot parse JSON data
 * from requests, responses, or configuration sources.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 400 (Bad Request) or HTTP 500 (Internal Server Error) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>Malformed JSON in request body</li>
 *   <li>Invalid JSON structure</li>
 *   <li>Type mismatches during deserialization</li>
 *   <li>Missing required JSON fields</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#JSON_PARSING_ERROR
 */
public class JsonParsingException extends BaseException {

    /**
     * Constructs a new JsonParsingException with the specified details.
     * 
     * @param details descriptive message about the parsing error
     */
    public JsonParsingException(String details) {
        super(ErrorCode.JSON_PARSING_ERROR, details);
    }

    /**
     * Constructs a new JsonParsingException with details and cause.
     * 
     * @param details descriptive message about the parsing error
     * @param cause the underlying cause of the exception
     */
    public JsonParsingException(String details, Throwable cause) {
        super(ErrorCode.JSON_PARSING_ERROR, details, cause);
    }
}
