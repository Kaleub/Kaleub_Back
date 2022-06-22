package com.photory.repository;

import com.photory.domain.Participate;
import com.photory.domain.Room;
import com.photory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, Long> {

    Optional<Participate> findByRoomAndUser(Room room, User user);
    ArrayList<Participate> findAllByUser(User user);
    ArrayList<Participate> findAllByRoom(Room room);
}
