package com.photory.controller.room.dto.request;

import lombok.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRoomRequestDto {

    @Size(min = 4, max = 8, message = "제목은 4~8 글자만 가능합니다.")
    private String title;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{6,15}",
            message = "비밀번호는 영문과 숫자 조합으로 6~15자리까지 가능합니다.")
    private String password;

    @Builder(builderMethodName = "testBuilder")
    public CreateRoomRequestDto(String title, String password) {
        this.title = title;
        this.password = password;
    }
}
