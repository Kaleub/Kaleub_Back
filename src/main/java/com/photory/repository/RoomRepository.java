package com.photory.repository;

import com.photory.domain.Room;
import com.photory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByCode(String code);
    Optional<Room> findByOwnerUser(User user);
}
