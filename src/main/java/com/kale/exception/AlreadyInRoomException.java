package com.kale.exception;

public class AlreadyInRoomException extends RuntimeException {

    private static final String MESSAGE = "이미 참가중인 방입니다.";

    public AlreadyInRoomException() {
        super(MESSAGE);
    }
}