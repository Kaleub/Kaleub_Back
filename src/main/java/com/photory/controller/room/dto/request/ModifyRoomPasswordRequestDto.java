package com.photory.controller.room.dto.request;

import lombok.*;

import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyRoomPasswordRequestDto {

    private Long roomId;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{6,15}",
            message = "비밀번호는 영문과 숫자 조합으로 6~15자리까지 가능합니다.")
    private String afterPassword;

    @Builder(builderMethodName = "testBuilder")
    public ModifyRoomPasswordRequestDto(Long roomId, String afterPassword) {
        this.roomId = roomId;
        this.afterPassword = afterPassword;
    }
}
