package com.kale.exception;

public class LoginException extends RuntimeException {

    private static final String MESSAGE = "로그인 실패입니다.";

    public LoginException() {
        super(MESSAGE);
    }
}
