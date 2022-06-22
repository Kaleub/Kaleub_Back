package com.photory.dto.response.feed;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class GetFeedResDto {

    private Long roomId;
    private Long userId;
    private String title;
    private String content;
    private ArrayList<String> imageUrls;

    @Builder
    public GetFeedResDto(Long roomId, Long userId, String title, String content, ArrayList<String> imageUrls) {
        this.roomId = roomId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static GetFeedResDto of(Long roomId, Long userId, String title, String content, ArrayList<String> imageUrls) {
        GetFeedResDto response = GetFeedResDto.builder()
                .roomId(roomId)
                .userId(userId)
                .title(title)
                .content(content)
                .imageUrls(imageUrls)
                .build();
        return response;
    }
}
