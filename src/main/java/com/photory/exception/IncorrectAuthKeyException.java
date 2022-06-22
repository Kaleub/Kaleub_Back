package com.photory.exception;

public class IncorrectAuthKeyException extends RuntimeException{

    private static final String MESSAGE = "인증번호를 다시 입력하세요.";

    public IncorrectAuthKeyException() { super(MESSAGE); }
}
