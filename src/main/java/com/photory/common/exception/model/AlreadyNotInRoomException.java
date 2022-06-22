package com.photory.common.exception.model;

public class AlreadyNotInRoomException extends RuntimeException {

    private static final String MESSAGE = "이미 나간 방입니다.";

    public AlreadyNotInRoomException() {
        super(MESSAGE);
    }
}