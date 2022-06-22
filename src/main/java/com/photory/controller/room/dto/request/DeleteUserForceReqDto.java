package com.photory.controller.room.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteUserForceReqDto {

    private Long roomId;
    private Long deletedUserId;
}
