package com.kale.dto.response.feed;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
@Getter
public class SelectFeedResDto {

    private Long roomId;
    private Long userId;
    private String title;
    private String content;
    private ArrayList<String> imageUrls;

    @Builder
    public SelectFeedResDto(Long roomId, Long userId, String title, String content, ArrayList<String> imageUrls) {
        this.roomId = roomId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }
}
