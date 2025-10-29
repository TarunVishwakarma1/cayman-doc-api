
package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

public class CabinetConnectionException extends BaseException {
    
    public CabinetConnectionException(String details) {
        super(ErrorCode.CABINET_CONNECTION_ERROR, details);
    }

    public CabinetConnectionException(String details, Throwable cause) {
        super(ErrorCode.CABINET_CONNECTION_ERROR, details, cause);
    }
}
