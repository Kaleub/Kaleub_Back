package com.photory.exception;

public class ExceedRoomCapacityException extends RuntimeException {

    private static final String MESSAGE = "방 최대 인원 초과입니다.";

    public ExceedRoomCapacityException() {
        super(MESSAGE);
    }
}