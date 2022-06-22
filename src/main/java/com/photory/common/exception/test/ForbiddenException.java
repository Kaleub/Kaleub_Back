package com.photory.common.exception.test;

import com.photory.common.exception.ErrorCode;

public class ForbiddenException extends PhotoryException {

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN_EXCEPTION);
    }
}