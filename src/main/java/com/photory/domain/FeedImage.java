package com.photory.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="FEED_ID")
    private Feed feed;

    @Column(nullable = false)
    private String imageUrl;

    @Builder
    public FeedImage(Feed feed, String imageUrl) {
        this.feed = feed;
        this.imageUrl = imageUrl;
    }

    public static FeedImage of(Feed feed, String imageUrl) {
        return FeedImage.builder()
                .feed(feed)
                .imageUrl(imageUrl)
                .build();
    }
}
