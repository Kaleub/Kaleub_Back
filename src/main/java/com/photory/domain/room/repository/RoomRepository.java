package com.photory.domain.room.repository;

import com.photory.domain.room.Room;
import com.photory.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByCode(String code);
    Optional<Room> findByOwnerUser(User user);
}
