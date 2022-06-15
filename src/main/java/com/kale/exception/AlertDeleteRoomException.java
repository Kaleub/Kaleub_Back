package com.kale.exception;

public class AlertDeleteRoomException extends RuntimeException {

    private static final String MESSAGE = "방을 나갈 경우 모든 데이터가 사라집니다.";

    public AlertDeleteRoomException() {
        super(MESSAGE);
    }
}