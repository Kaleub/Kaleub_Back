package com.photory.common.exception.model;

import com.photory.common.exception.ErrorCode;

public class InternalServerException extends PhotoryException {

    public InternalServerException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_EXCEPTION);
    }

    public InternalServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
