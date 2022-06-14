package com.kale.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorDto {

    private int status;
    private String message;

    @Builder
    public ErrorDto(int status, String message) {
        this.status = status;
        this.message = message;
    }
}