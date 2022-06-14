package com.kale.repository;

import com.kale.model.Participate;
import com.kale.model.Room;
import com.kale.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, Long> {

    Optional<Participate> findByRoomAndUser(Room room, User user);
    ArrayList<Participate> findAllByUser(User user);
    ArrayList<Participate> findAllByRoom(Room room);
}
