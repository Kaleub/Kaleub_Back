package com.photory.common.exception.model;

public class UnAuthenticatedEmailException extends RuntimeException {

    private static final String MESSAGE = "인증되지 않은 이메일입니다.";

    public UnAuthenticatedEmailException() { super(MESSAGE); }
}
