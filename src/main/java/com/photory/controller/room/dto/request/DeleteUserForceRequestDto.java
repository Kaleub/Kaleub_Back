package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteUserForceRequestDto {

    private Long roomId;
    private Long deletedUserId;

    @Builder(builderMethodName = "testBuilder")
    public DeleteUserForceRequestDto(Long roomId, Long deletedUserId) {
        this.roomId = roomId;
        this.deletedUserId = deletedUserId;
    }
}
