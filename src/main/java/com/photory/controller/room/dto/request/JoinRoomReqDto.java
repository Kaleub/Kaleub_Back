package com.photory.controller.room.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinRoomReqDto {

    private String code;
    private String password;

    @Builder(builderMethodName = "testBuilder")
    public JoinRoomReqDto(String code, String password) {
        this.code = code;
        this.password = password;
    }
}
