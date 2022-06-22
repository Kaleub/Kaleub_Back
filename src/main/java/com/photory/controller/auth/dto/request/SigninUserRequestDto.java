package com.photory.controller.auth.dto.request;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SigninUserRequestDto {

    private String email;
    private String password;
}
