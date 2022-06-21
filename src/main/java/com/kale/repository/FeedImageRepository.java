package com.kale.repository;

import com.kale.domain.Feed;
import com.kale.domain.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    ArrayList<FeedImage> findAllByFeed(Feed feed);
}
