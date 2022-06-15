package com.kale.dto.request.auth;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class AuthEmailCompleteReqDto {

    private String authKey;
}
