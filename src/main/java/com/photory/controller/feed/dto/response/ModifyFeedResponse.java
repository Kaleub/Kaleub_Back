package com.photory.controller.feed.dto.response;

import com.photory.domain.feed.Feed;
import lombok.*;

import java.util.ArrayList;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyFeedResponse {

    private Long roomId;
    private Long userId;
    private String title;
    private String content;
    private ArrayList<String> imageUrls;

    @Builder
    public ModifyFeedResponse(Long roomId, Long userId, String title, String content, ArrayList<String> imageUrls) {
        this.roomId = roomId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static ModifyFeedResponse of(Feed feed, ArrayList<String> imageUrls) {
        ModifyFeedResponse response = ModifyFeedResponse.builder()
                .roomId(feed.getRoom().getId())
                .userId(feed.getUser().getId())
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrls(imageUrls)
                .build();
        return response;
    }
}
