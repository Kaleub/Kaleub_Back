package com.photory.domain.feed.repository;

import com.photory.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}
