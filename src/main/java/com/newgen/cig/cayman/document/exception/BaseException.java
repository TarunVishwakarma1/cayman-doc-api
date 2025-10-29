package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseException extends RuntimeException {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseException.class);
    
    private final ErrorCode errorCode;
    private final String details;

    protected BaseException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage() + (details != null ? ": " + details : ""));
        this.errorCode = errorCode;
        this.details = details;
        logger.error("{} created - Code: {}, Message: {}, Details: {}", 
                this.getClass().getSimpleName(), errorCode.getCode(), errorCode.getMessage(), details);
    }

    protected BaseException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage() + (details != null ? ": " + details : ""), cause);
        this.errorCode = errorCode;
        this.details = details;
        logger.error("{} created - Code: {}, Message: {}, Details: {}", 
                this.getClass().getSimpleName(), errorCode.getCode(), errorCode.getMessage(), details, cause);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}
