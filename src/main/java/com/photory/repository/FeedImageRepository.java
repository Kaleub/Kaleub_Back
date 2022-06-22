package com.photory.repository;

import com.photory.domain.Feed;
import com.photory.domain.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    ArrayList<FeedImage> findAllByFeed(Feed feed);
}
