package com.photory.controller.room.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DelegateOwnerRequestDto {

    private Long roomId;
    private Long delegatedUserId;

    @Builder(builderMethodName = "testBuilder")
    public DelegateOwnerRequestDto(Long roomId, Long delegatedUserId) {
        this.roomId = roomId;
        this.delegatedUserId = delegatedUserId;
    }
}
