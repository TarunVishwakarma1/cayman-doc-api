package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class DocumentNotFoundException extends BaseException {
    
    public DocumentNotFoundException(String details) {
        super(ErrorCode.DOCUMENT_NOT_FOUND, details);
    }

    public DocumentNotFoundException(String details, Throwable cause) {
        super(ErrorCode.DOCUMENT_NOT_FOUND, details, cause);
    }
}
