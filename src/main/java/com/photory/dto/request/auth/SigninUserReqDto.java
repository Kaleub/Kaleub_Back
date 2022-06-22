package com.photory.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninUserReqDto {

    private String email;
    private String password;
}
