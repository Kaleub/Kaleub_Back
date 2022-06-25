package com.photory.controller.feed.dto.response;

import com.photory.domain.collection.FeedImageCollection;
import com.photory.domain.common.collection.ScrollPaginationCollection;
import com.photory.domain.feed.Feed;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetFeedsResponse {

    private static final long LAST_CURSOR = -1L;

    private List<FeedsInfoResponse> contents = new ArrayList<>();
    private long totalElements;
    private long nextCursor;

    private GetFeedsResponse(List<FeedsInfoResponse> contents, long totalElements, long nextCursor) {
        this.contents = contents;
        this.totalElements = totalElements;
        this.nextCursor = nextCursor;
    }

    public static GetFeedsResponse of(ScrollPaginationCollection<Feed> feedsScroll, FeedImageCollection feedImages, long totalElements) {
        if (feedsScroll.isLastScroll()) {
            return GetFeedsResponse.newLastScroll(feedsScroll.getCurrentScrollItems(), feedImages, totalElements);
        }
        return GetFeedsResponse.newScrollHasNext(feedsScroll.getCurrentScrollItems(), feedImages, totalElements, feedsScroll.getNextCursor().getId());
    }

    private static GetFeedsResponse newLastScroll(List<Feed> feedsScroll, FeedImageCollection feedImages, long totalElements) {
        return newScrollHasNext(feedsScroll, feedImages, totalElements, LAST_CURSOR);
    }

    private static GetFeedsResponse newScrollHasNext(List<Feed> feedsScroll, FeedImageCollection feedImages, long totalElements, long nextCursor) {
        return new GetFeedsResponse(getContents(feedsScroll, feedImages), totalElements, nextCursor);
    }

    private static List<FeedsInfoResponse> getContents(List<Feed> feedsScroll, FeedImageCollection feedImages) {
        return feedsScroll.stream()
                .map(feed -> FeedsInfoResponse.of(feed, feedImages.getImagesByFeedId(feed.getId())))
                .collect(Collectors.toList());
    }
}
