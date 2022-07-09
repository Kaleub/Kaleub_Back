package com.photory.domain.feed.repository;

import com.photory.domain.feed.Feed;
import com.photory.domain.room.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByRoomAndIdLessThanOrderByIdDesc(Room room, Long lastFeedId, PageRequest pageRequest);

    long countAllByRoom(Room room);
}
