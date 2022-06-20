package com.kale.service;

import com.kale.exception.LoginException;
import com.kale.exception.NotFoundRoomException;
import com.kale.domain.Room;
import com.kale.domain.User;
import com.kale.repository.RoomRepository;
import com.kale.repository.UserRepository;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public class RoomServiceUtils {

    public static User findUserByEmail(UserRepository userRepository, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new LoginException();
        }

        return user.get();
    }

    public static Room findRoomByRoomId(RoomRepository roomRepository, Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);

        if (room.isEmpty()) {
            throw new NotFoundRoomException();
        }

        return room.get();
    }
}
