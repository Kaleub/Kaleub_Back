package com.kale.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ValidateEmailReqDto {

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @NotNull(message = "이메일은 필수 입력값입니다.")
    private String email;
}
