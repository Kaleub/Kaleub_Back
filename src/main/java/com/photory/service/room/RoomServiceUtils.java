package com.photory.service.room;

import com.photory.common.exception.test.NotFoundException;
import com.photory.common.exception.test.UnAuthorizedException;
import com.photory.domain.room.Room;
import com.photory.domain.user.User;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.repository.UserRepository;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static com.photory.common.exception.ErrorCode.NOT_FOUND_ROOM_EXCEPTION;

@NoArgsConstructor
public class RoomServiceUtils {

    public static User findUserByEmail(UserRepository userRepository, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new UnAuthorizedException("로그인 실패입니다.");
        }

        return user.get();
    }

    public static Room findRoomByRoomId(RoomRepository roomRepository, Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);

        if (room.isEmpty()) {
            throw new NotFoundException(String.format("존재하지 않는 방 (%s) 입니다", roomId), NOT_FOUND_ROOM_EXCEPTION);
        }

        return room.get();
    }
}
