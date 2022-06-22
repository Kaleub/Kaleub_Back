package com.photory.common.exception.test;

import com.photory.common.exception.ErrorCode;

public class NotFoundException extends PhotoryException {

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND_EXCEPTION);
    }
}