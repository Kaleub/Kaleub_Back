package com.photory.dto.request.room;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinRoomReqDto {

    private String code;
    private String password;
}
