package com.kale.exception;

public class NotFoundRoomException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 방입니다.";

    public NotFoundRoomException() {
        super(MESSAGE);
    }
}