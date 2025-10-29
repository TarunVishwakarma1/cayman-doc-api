package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class SessionExpiredException extends BaseException {
    
    public SessionExpiredException(String details) {
        super(ErrorCode.SESSION_EXPIRED, details);
    }

    public SessionExpiredException(String details, Throwable cause) {
        super(ErrorCode.SESSION_EXPIRED, details, cause);
    }
}
