package com.kale.dto.response.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginUserResDto {

    private String token;

    @Builder
    public LoginUserResDto(String token) {
        this.token = token;
    }
}
