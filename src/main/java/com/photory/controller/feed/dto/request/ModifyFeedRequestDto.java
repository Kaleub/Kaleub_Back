package com.photory.controller.feed.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyFeedRequestDto {

    private Long feedId;

    @NotBlank(message = "제목을 입력해야합니다.")
    private String title;

    @NotNull(message = "내용은 null 값일 수 없습니다.")
    private String content;

    @Builder(builderMethodName = "testBuilder")
    public ModifyFeedRequestDto(Long feedId, String title, String content) {
        this.feedId = feedId;
        this.title = title;
        this.content = content;
    }
}
