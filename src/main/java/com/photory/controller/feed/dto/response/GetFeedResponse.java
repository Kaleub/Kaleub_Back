package com.photory.controller.feed.dto.response;

import com.photory.domain.feed.Feed;
import lombok.*;

import java.util.ArrayList;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetFeedResponse {

    private Long roomId;
    private Long userId;
    private String title;
    private String content;
    private ArrayList<String> imageUrls;

    @Builder
    public GetFeedResponse(Long roomId, Long userId, String title, String content, ArrayList<String> imageUrls) {
        this.roomId = roomId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static GetFeedResponse of(Feed feed, ArrayList<String> imageUrls) {
        GetFeedResponse response = GetFeedResponse.builder()
                .roomId(feed.getRoom().getId())
                .userId(feed.getUser().getId())
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrls(imageUrls)
                .build();
        return response;
    }
}
