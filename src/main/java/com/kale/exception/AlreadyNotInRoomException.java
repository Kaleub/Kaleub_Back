package com.kale.exception;

public class AlreadyNotInRoomException extends RuntimeException {

    private static final String MESSAGE = "이미 나간 방입니다.";

    public AlreadyNotInRoomException() {
        super(MESSAGE);
    }
}