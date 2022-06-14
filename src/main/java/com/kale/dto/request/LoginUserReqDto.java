package com.kale.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginUserReqDto {

    private String email;
    private String password;
}
