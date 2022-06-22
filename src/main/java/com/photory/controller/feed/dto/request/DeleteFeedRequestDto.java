package com.photory.controller.feed.dto.request;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteFeedRequestDto {

    private Long feedId;
}
