package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DelegateOwnerRequestDto {

    private Long roomId;
    private Long delegatedUserId;
}
