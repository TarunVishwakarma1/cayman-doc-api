package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class JsonParsingException extends BaseException {
    
    public JsonParsingException(String details) {
        super(ErrorCode.JSON_PARSING_ERROR, details);
    }

    public JsonParsingException(String details, Throwable cause) {
        super(ErrorCode.JSON_PARSING_ERROR, details, cause);
    }
}
