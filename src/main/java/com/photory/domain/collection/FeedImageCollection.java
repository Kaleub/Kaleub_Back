package com.photory.domain.collection;

import com.photory.domain.feed.Feed;
import com.photory.domain.feedimage.FeedImage;
import com.photory.domain.feedimage.repository.FeedImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedImageCollection {

    /**
     * 피드에 포함된 이미지 객체 전달을 위한 Collection
     */

    private final Map<Long, List<FeedImage>> collection;

    public static FeedImageCollection of(List<Feed> feeds, FeedImageRepository feedImageRepository) {
        return new FeedImageCollection(
                feeds.stream()
                        .collect(
                                Collectors.toMap(
                                        feed -> feed.getId(),
                                        feed -> feedImageRepository.findAllByFeed(feed)
                                )
                        )
        );
    }

    public List<FeedImage> getImagesByFeedId(Long feedId) {
        return collection.get(feedId);
    }
}
