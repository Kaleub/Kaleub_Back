package com.photory.controller.auth.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SigninUserRequestDto {

    private String email;
    private String password;

    @Builder(builderMethodName = "testBuilder")
    public SigninUserRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
