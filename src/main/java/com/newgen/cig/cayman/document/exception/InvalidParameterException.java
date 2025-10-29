package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class InvalidParameterException extends BaseException {
    
    public InvalidParameterException(String details) {
        super(ErrorCode.INVALID_PARAMETER, details);
    }

    public InvalidParameterException(String details, Throwable cause) {
        super(ErrorCode.INVALID_PARAMETER, details, cause);
    }
}
