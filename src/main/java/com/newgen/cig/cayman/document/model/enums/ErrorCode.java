
package com.newgen.cig.cayman.document.model.enums;

import org.springframework.http.HttpStatus;

/**
 * Canonical error codes with default messages and HTTP statuses.
 *
 * <p>Each code is mapped to an {@link org.springframework.http.HttpStatus}
 * for consistent API responses.</p>
 */
public enum ErrorCode {
    // 4xx Client Errors
    BAD_REQUEST("ERR_001", "Bad request", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("ERR_002", "Invalid parameter provided", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("ERR_003", "Required parameter is missing", HttpStatus.BAD_REQUEST),
    INVALID_DOCUMENT_INDEX("ERR_004", "Invalid document index", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("ERR_005", "Invalid credentials provided", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("ERR_006", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED("ERR_007", "Session has expired", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_008", "Access forbidden", HttpStatus.FORBIDDEN),
    DOCUMENT_NOT_FOUND("ERR_009", "Document not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND("ERR_010", "Requested resource not found", HttpStatus.NOT_FOUND),
    TOO_MANY_REQUESTS("ERR_011", "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    PAYLOAD_TOO_LARGE("ERR_012", "Request payload too large", HttpStatus.PAYLOAD_TOO_LARGE),
    
    // 5xx Server Errors
    INTERNAL_SERVER_ERROR("ERR_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    CABINET_CONNECTION_ERROR("ERR_501", "Failed to connect to cabinet", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("ERR_502", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("ERR_503", "External service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    XML_PARSING_ERROR("ERR_504", "Failed to parse XML response", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PARSING_ERROR("ERR_505", "Failed to parse JSON response", HttpStatus.INTERNAL_SERVER_ERROR),
    ENCRYPTION_ERROR("ERR_506", "Encryption operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DECRYPTION_ERROR("ERR_507", "Decryption operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_GENERATION_ERROR("ERR_508", "Key generation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SIGNATURE_ERROR("ERR_509", "Signature operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DOCUMENT_FETCH_ERROR("ERR_510", "Failed to fetch document", HttpStatus.INTERNAL_SERVER_ERROR),
    CONFIGURATION_ERROR("ERR_511", "Configuration error", HttpStatus.INTERNAL_SERVER_ERROR),
    TIMEOUT_ERROR("ERR_512", "Operation timeout", HttpStatus.REQUEST_TIMEOUT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
