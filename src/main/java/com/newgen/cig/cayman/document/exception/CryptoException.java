package com.newgen.cig.cayman.document.exception;


import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class CryptoException extends BaseException {

    public CryptoException(String details) {
        super(ErrorCode.ENCRYPTION_ERROR, details);
    }

    public CryptoException(String details, Throwable cause) {
        super(ErrorCode.ENCRYPTION_ERROR, details, cause);
    }

    public CryptoException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }

    public CryptoException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode, details, cause);
    }
}
