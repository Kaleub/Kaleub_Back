package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinRoomRequestDto {

    private String code;
    private String password;

    @Builder(builderMethodName = "testBuilder")
    public JoinRoomRequestDto(String code, String password) {
        this.code = code;
        this.password = password;
    }
}
