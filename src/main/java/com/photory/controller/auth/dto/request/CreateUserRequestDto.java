package com.photory.controller.auth.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateUserRequestDto {

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @NotNull(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{6,15}",
            message = "비밀번호는 영문과 숫자 조합으로 6~15자리까지 가능합니다.")
    private String password;

    @NotEmpty(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @Builder(builderMethodName = "testBuilder")
    public CreateUserRequestDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
