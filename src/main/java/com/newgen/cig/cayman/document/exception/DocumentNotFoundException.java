package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when a requested document cannot be found in the cabinet.
 * 
 * <p>This exception is raised when attempting to retrieve a document using
 * an invalid or non-existent document index.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 404 (Not Found) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>Invalid document index provided</li>
 *   <li>Document has been deleted from the cabinet</li>
 *   <li>Insufficient permissions to access the document</li>
 *   <li>Document exists in a different cabinet or volume</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#DOCUMENT_NOT_FOUND
 */
public class DocumentNotFoundException extends BaseException {

    /**
     * Constructs a new DocumentNotFoundException with the specified details.
     * 
     * @param details descriptive message about the missing document
     */
    public DocumentNotFoundException(String details) {
        super(ErrorCode.DOCUMENT_NOT_FOUND, details);
    }

    /**
     * Constructs a new DocumentNotFoundException with details and cause.
     * 
     * @param details descriptive message about the missing document
     * @param cause the underlying cause of the exception
     */
    public DocumentNotFoundException(String details, Throwable cause) {
        super(ErrorCode.DOCUMENT_NOT_FOUND, details, cause);
    }
}
