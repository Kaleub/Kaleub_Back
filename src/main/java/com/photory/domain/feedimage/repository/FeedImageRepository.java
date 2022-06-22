package com.photory.domain.feedimage.repository;

import com.photory.domain.feed.Feed;
import com.photory.domain.feedimage.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    ArrayList<FeedImage> findAllByFeed(Feed feed);
}
