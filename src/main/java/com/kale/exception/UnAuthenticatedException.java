package com.kale.exception;

public class UnAuthenticatedException extends RuntimeException{

    private static final String MESSAGE = "인증되지 않은 이메일입니다.";

    public UnAuthenticatedException() { super(MESSAGE); }
}
