package com.photory.common.exception.model;

import com.photory.common.exception.ErrorCode;

public class UnAuthorizedException extends PhotoryException {

    public UnAuthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnAuthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
}