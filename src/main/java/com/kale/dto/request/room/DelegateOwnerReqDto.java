package com.kale.dto.request.room;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DelegateOwnerReqDto {

    private Long roomId;
    private String delegatedUserEmail;
}
