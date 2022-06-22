package com.photory.dto.request.room;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DelegateOwnerReqDto {

    private Long roomId;
    private Long delegatedUserId;
}
