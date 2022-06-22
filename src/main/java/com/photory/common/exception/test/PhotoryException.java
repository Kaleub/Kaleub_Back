package com.photory.common.exception.test;

import com.photory.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public abstract class PhotoryException extends RuntimeException{

    private final ErrorCode errorCode;

    public PhotoryException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return errorCode.getStatus();
    }
}