package com.kale.dto.request.room;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateRoomReqDto {

    private String title;
    private String password;
}
