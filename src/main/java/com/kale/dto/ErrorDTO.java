package com.kale.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorDTO {

    private int status;
    private String message;

    @Builder
    public ErrorDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }
}