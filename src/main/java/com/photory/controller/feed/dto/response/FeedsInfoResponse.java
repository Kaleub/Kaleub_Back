package com.photory.controller.feed.dto.response;

import com.photory.common.dto.AuditingTimeResponse;
import com.photory.domain.collection.FeedImageCollection;
import com.photory.domain.feed.Feed;
import com.photory.domain.feedimage.FeedImage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedsInfoResponse extends AuditingTimeResponse {

    private Long id;
    private String title;
    private String content;
    private ArrayList<String> imageUrls;

    @Builder
    public FeedsInfoResponse(Long id, String title, String content, ArrayList<String> imageUrls) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static FeedsInfoResponse of(Feed feed, List<FeedImage> feedImages) {
        ArrayList<String> tmp = new ArrayList<>();
        for (FeedImage feedImage : feedImages) {
            tmp.add(feedImage.getImageUrl());
        }
        FeedsInfoResponse response = FeedsInfoResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrls(tmp)
                .build();
        response.setBaseTime(feed);
        return response;
    }
}
