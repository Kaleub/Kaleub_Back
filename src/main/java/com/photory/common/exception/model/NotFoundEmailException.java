package com.photory.common.exception.model;

public class NotFoundEmailException extends RuntimeException {

    private static final String MESSAGE = "가입되지 않은 이메일입니다.";

    public NotFoundEmailException() {
        super(MESSAGE);
    }
}