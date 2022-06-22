package com.photory.common.exception.model;

public class ExistingEmailException extends RuntimeException {

    private static final String MESSAGE = "이메일 중복입니다.";

    public ExistingEmailException() {
        super(MESSAGE);
    }
}