package com.photory.domain.participate.repository;

import com.photory.domain.participate.Participate;
import com.photory.domain.room.Room;
import com.photory.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, Long> {

    Optional<Participate> findByRoomAndUser(Room room, User user);
    ArrayList<Participate> findAllByUser(User user);
    ArrayList<Participate> findAllByRoom(Room room);
}
