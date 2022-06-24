package com.photory.domain.feed.repository;

import com.photory.domain.feed.Feed;
import com.photory.domain.room.Room;
import com.photory.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByUserAndRoomAndIdLessThanOrderByIdDesc(User user, Room room, Long lastFeedId, PageRequest pageRequest);
    long countAllByUserAndRoom(User user, Room room);
}
