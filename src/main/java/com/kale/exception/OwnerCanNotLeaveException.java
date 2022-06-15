package com.kale.exception;

public class OwnerCanNotLeaveException extends RuntimeException {

    private static final String MESSAGE = "방장은 다른 참가자가 있으면 방을 나갈 수 없습니다.";

    public OwnerCanNotLeaveException() {
        super(MESSAGE);
    }
}