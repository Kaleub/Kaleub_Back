package com.kale.exception;

public class NotFeedOwnerException extends RuntimeException {

    private static final String MESSAGE = "피드 작성자가 아닙니다.";

    public NotFeedOwnerException() {
        super(MESSAGE);
    }
}