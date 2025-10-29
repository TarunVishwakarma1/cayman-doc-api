
package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class MissingParameterException extends BaseException {
    
    public MissingParameterException(String details) {
        super(ErrorCode.MISSING_PARAMETER, details);
    }

    public MissingParameterException(String details, Throwable cause) {
        super(ErrorCode.MISSING_PARAMETER, details, cause);
    }
}
