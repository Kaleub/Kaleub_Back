package com.photory.common.exception.test;

import com.photory.common.exception.ErrorCode;

public class ConflictException extends PhotoryException {

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT_EXCEPTION);
    }
}