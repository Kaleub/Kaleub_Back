package com.photory.common.exception.model;

public class NotFoundFeedException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 피드입니다.";

    public NotFoundFeedException() {
        super(MESSAGE);
    }
}