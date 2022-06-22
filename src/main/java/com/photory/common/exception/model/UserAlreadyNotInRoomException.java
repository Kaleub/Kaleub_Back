package com.photory.common.exception.model;

public class UserAlreadyNotInRoomException extends RuntimeException {

    private static final String MESSAGE = "이미 나간 사용자입니다.";
    public UserAlreadyNotInRoomException() { super(MESSAGE); }

}
