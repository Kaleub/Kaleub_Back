package com.photory.exception;

public class NotAloneException extends RuntimeException {

    private static final String MESSAGE = "방에 다른 참가자가 있습니다.";

    public NotAloneException() {
        super(MESSAGE);
    }
}