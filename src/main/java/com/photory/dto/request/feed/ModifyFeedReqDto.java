package com.photory.dto.request.feed;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ModifyFeedReqDto {

    private Long feedId;

    @NotBlank(message = "제목을 입력해야합니다.")
    private String title;

    @NotNull(message = "내용은 null 값일 수 없습니다.")
    private String content;
}
