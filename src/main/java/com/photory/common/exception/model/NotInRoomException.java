package com.photory.common.exception.model;

public class NotInRoomException extends RuntimeException {

    private static final String MESSAGE = "참가중인 방이 아닙니다.";

    public NotInRoomException() {
        super(MESSAGE);
    }
}