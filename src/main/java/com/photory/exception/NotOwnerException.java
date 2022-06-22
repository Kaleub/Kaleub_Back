package com.photory.exception;

public class NotOwnerException extends RuntimeException {

    private static final String MESSAGE = "방장이 아닙니다.";

    public NotOwnerException() {
        super(MESSAGE);
    }
}