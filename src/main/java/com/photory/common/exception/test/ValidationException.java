package com.photory.common.exception.test;

import com.photory.common.exception.ErrorCode;

public class ValidationException extends PhotoryException {

    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_EXCEPTION);
    }
}