package com.photory.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseDto {

    private int status;
    private String message;
    private Object data;

    @Builder
    public ResponseDto(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
