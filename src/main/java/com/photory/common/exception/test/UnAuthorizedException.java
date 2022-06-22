package com.photory.common.exception.test;

import com.photory.common.exception.ErrorCode;

public class UnAuthorizedException extends PhotoryException {

    public UnAuthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
}