package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveRoomRequestDto {

    private Long roomId;

    @Builder(builderMethodName = "testBuilder")
    public LeaveRoomRequestDto(Long roomId) {
        this.roomId = roomId;
    }
}
