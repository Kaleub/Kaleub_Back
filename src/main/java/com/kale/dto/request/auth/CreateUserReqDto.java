package com.kale.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CreateUserReqDto {

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @NotNull(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Pattern(regexp="^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{6,15}",
            message = "비밀번호는 영문과 숫자 조합으로 6~15자리까지 가능합니다")
    private String password;
}
