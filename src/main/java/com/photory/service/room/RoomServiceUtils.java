package com.photory.service.room;

import com.photory.common.exception.model.LoginException;
import com.photory.common.exception.model.NotFoundRoomException;
import com.photory.domain.room.Room;
import com.photory.domain.user.User;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.repository.UserRepository;
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
