package com.photory.common.exception.model;

public class InvalidPasswordException extends RuntimeException {

    private static final String MESSAGE = "비밀번호를 다시 입력하세요.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }
}