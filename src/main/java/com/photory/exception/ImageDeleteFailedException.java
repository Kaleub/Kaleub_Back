package com.photory.exception;

public class ImageDeleteFailedException extends RuntimeException {

    private static final String MESSAGE = "파일 업로드에 실패했습니다.";

    public ImageDeleteFailedException() {
        super(MESSAGE);
    }
}