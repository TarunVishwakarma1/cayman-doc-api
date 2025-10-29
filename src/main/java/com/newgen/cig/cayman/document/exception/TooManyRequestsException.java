package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class TooManyRequestsException extends BaseException {
    
    public TooManyRequestsException(String details) {
        super(ErrorCode.TOO_MANY_REQUESTS, details);
    }

    public TooManyRequestsException(String details, Throwable cause) {
        super(ErrorCode.TOO_MANY_REQUESTS, details, cause);
    }
}
