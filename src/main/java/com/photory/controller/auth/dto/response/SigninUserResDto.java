package com.photory.controller.auth.dto.response;

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

    public static SigninUserResDto of(String token) {
        SigninUserResDto response = SigninUserResDto.builder()
                .token(token)
                .build();
        return response;
    }
}
