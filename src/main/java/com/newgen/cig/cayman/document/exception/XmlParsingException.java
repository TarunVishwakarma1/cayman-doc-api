package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class XmlParsingException extends BaseException {
    
    public XmlParsingException(String details) {
        super(ErrorCode.XML_PARSING_ERROR, details);
    }

    public XmlParsingException(String details, Throwable cause) {
        super(ErrorCode.XML_PARSING_ERROR, details, cause);
    }
}
