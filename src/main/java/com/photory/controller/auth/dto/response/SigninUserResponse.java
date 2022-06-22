package com.photory.controller.auth.dto.response;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SigninUserResponse {

    private String token;

    @Builder
    public SigninUserResponse(String token) {
        this.token = token;
    }

    public static SigninUserResponse of(String token) {
        SigninUserResponse response = SigninUserResponse.builder()
                .token(token)
                .build();
        return response;
    }
}
