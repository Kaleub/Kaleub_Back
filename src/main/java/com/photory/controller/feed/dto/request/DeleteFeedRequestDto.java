package com.photory.controller.feed.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteFeedRequestDto {

    private Long feedId;

    @Builder(builderMethodName = "testBuilder")
    public DeleteFeedRequestDto(Long feedId) {
        this.feedId = feedId;
    }
}
