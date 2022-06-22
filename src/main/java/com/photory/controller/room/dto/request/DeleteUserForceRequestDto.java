package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteUserForceRequestDto {

    private Long roomId;
    private Long deletedUserId;
}
