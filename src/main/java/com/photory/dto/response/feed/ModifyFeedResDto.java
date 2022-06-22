package com.photory.dto.response.feed;

import com.photory.domain.Feed;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class ModifyFeedResDto {

    private Long roomId;
    private Long userId;
    private String title;
    private String content;
    private ArrayList<String> imageUrls;

    @Builder
    public ModifyFeedResDto(Long roomId, Long userId, String title, String content, ArrayList<String> imageUrls) {
        this.roomId = roomId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static ModifyFeedResDto of(Feed feed, ArrayList<String> imageUrls) {
        ModifyFeedResDto response = ModifyFeedResDto.builder()
                .roomId(feed.getRoom().getId())
                .userId(feed.getUser().getId())
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrls(imageUrls)
                .build();
        return response;
    }
}
