package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisableRoomRequestDto {

    private Long roomId;

    @Builder(builderMethodName = "testBuilder")
    public DisableRoomRequestDto(Long roomId) {
        this.roomId = roomId;
    }
}
