package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when XML parsing operations fail.
 * 
 * <p>This exception is raised when the application cannot parse XML responses
 * from the OmniDocs cabinet or encounters malformed XML data.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 500 (Internal Server Error) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>Malformed XML response from cabinet</li>
 *   <li>Missing expected XML elements or attributes</li>
 *   <li>Invalid XML structure</li>
 *   <li>Character encoding issues</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#XML_PARSING_ERROR
 */
public class XmlParsingException extends BaseException {

    /**
     * Constructs a new XmlParsingException with the specified details.
     * 
     * @param details descriptive message about the parsing error
     */
    public XmlParsingException(String details) {
        super(ErrorCode.XML_PARSING_ERROR, details);
    }

    /**
     * Constructs a new XmlParsingException with details and cause.
     * 
     * @param details descriptive message about the parsing error
     * @param cause the underlying cause of the exception
     */
    public XmlParsingException(String details, Throwable cause) {
        super(ErrorCode.XML_PARSING_ERROR, details, cause);
    }
}
