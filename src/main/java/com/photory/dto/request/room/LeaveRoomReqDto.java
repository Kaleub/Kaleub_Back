package com.photory.dto.request.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LeaveRoomReqDto {

    private Long roomId;

    @Builder(builderMethodName = "testBuilder")
    public LeaveRoomReqDto(Long roomId) {
        this.roomId = roomId;
    }
}
