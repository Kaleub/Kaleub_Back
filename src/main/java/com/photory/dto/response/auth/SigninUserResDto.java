package com.photory.dto.response.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninUserResDto {

    private String token;

    @Builder
    public SigninUserResDto(String token) {
        this.token = token;
    }
}
