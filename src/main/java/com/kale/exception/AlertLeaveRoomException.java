package com.kale.exception;

public class AlertLeaveRoomException extends RuntimeException {

    private static final String MESSAGE = "방 비활성화 경고입니다.";

    public AlertLeaveRoomException() {
        super(MESSAGE);
    }
}