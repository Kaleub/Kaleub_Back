package com.photory.dto.request.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@Getter
public class ModifyRoomPasswordReqDto {

    private Long roomId;
    private String beforePassword;

    @Pattern(regexp="^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{6,15}",
            message = "비밀번호는 영문과 숫자 조합으로 6~15자리까지 가능합니다.")
    private String afterPassword;

    @Builder(builderMethodName = "testBuilder")
    public ModifyRoomPasswordReqDto(Long roomId, String beforePassword, String afterPassword) {
        this.roomId = roomId;
        this.beforePassword = beforePassword;
        this.afterPassword = afterPassword;
    }
}
