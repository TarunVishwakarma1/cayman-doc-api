package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class ExternalServiceException extends BaseException {
    
    public ExternalServiceException(String details) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR, details);
    }

    public ExternalServiceException(String details, Throwable cause) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR, details, cause);
    }
}
