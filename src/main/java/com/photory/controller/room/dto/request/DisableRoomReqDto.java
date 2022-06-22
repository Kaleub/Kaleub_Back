package com.photory.controller.room.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DisableRoomReqDto {

    private Long roomId;

    @Builder(builderMethodName = "testBuilder")
    public DisableRoomReqDto(Long roomId) {
        this.roomId = roomId;
    }
}
