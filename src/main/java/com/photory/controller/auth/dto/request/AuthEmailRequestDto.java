package com.photory.controller.auth.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthEmailRequestDto {

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @NotNull(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Builder(builderMethodName = "testBuilder")
    public AuthEmailRequestDto(@Email(message = "올바르지 않은 이메일 형식입니다.") @NotNull(message = "이메일은 필수 입력값입니다.") String email) {
        this.email = email;
    }
}
