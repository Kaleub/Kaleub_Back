package com.photory.controller.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninUserReqDto {

    private String email;
    private String password;
}
