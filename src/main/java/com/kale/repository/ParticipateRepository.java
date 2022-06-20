package com.kale.repository;

import com.kale.domain.Participate;
import com.kale.domain.Room;
import com.kale.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, Long> {

    Optional<Participate> findByRoomAndUser(Room room, User user);
    ArrayList<Participate> findAllByUser(User user);
    ArrayList<Participate> findAllByRoom(Room room);
}
