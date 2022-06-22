package com.photory.common.exception.model;

public class InvalidFileException extends RuntimeException {

    private static final String MESSAGE = "잘못된 파일 확장자입니다.";

    public InvalidFileException() {
        super(MESSAGE);
    }
}