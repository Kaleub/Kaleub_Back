package com.photory.service;

import com.photory.exception.LoginException;
import com.photory.exception.NotFoundRoomException;
import com.photory.domain.Room;
import com.photory.domain.User;
import com.photory.repository.RoomRepository;
import com.photory.repository.UserRepository;
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
