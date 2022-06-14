package com.kale.service;

import com.kale.exception.AlreadyInRoomException;
import com.kale.exception.InvalidPasswordException;
import com.kale.exception.LoginException;
import com.kale.exception.NotFoundRoomException;
import com.kale.model.Participate;
import com.kale.model.Room;
import com.kale.model.User;
import com.kale.repository.ParticipateRepository;
import com.kale.repository.RoomRepository;
import com.kale.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ParticipateRepository participateRepository;
    private final PasswordEncoder passwordEncoder;

    public Room createRoom(String userEmail, String title, String password) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new LoginException();
        }

        Room room = Room.builder()
                .title(title)
                .password(passwordEncoder.encode(password))
                .code(createRoomCode())
                .ownerUser(user.get())
                .build();

        Room created = roomRepository.save(room);

        Participate participate = Participate.builder()
                .room(created)
                .user(user.get())
                .build();

        participateRepository.save(participate);

        return created;
    }

    public Room joinRoom(String userEmail, String code, String password) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new LoginException();
        }

        Optional<Room> room = roomRepository.findByCode(code);

        if (room.isPresent()) {
            if (passwordEncoder.matches(password, room.get().getPassword())) {
                Optional<Participate> participating = participateRepository.findByRoomAndUser(room.get(), user.get());
                if (participating.isPresent()) {
                    throw new AlreadyInRoomException();
                } else {
                    Participate participate = Participate.builder()
                            .room(room.get())
                            .user(user.get())
                            .build();

                    participateRepository.save(participate);

                    return room.get();
                }
            } else {
                throw new InvalidPasswordException();
            }
        } else {
            throw new NotFoundRoomException();
        }
    }

    public ArrayList<Room> getRooms(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new LoginException();
        }

        ArrayList<Room> rooms = new ArrayList<>();

        ArrayList<Participate> participates = participateRepository.findAllByUser(user.get());
        for (int i = 0; i < participates.size(); i++) {
            rooms.add(participates.get(i).getRoom());
        }

        return rooms;
    }

    private String createRoomCode() {
        String result;
        do {
            char[] tmp = new char[8];
            for(int i=0; i<tmp.length; i++) {
                int div = (int) Math.floor( Math.random() * 2 );
                if(div == 0) { // 0이면 숫자로
                    tmp[i] = (char) (Math.random() * 10 + '0') ;
                } else { //1이면 알파벳
                    tmp[i] = (char) (Math.random() * 26 + 'A') ;
                }
            }
            result = new String(tmp);
        } while(checkRoomCode(result));

        return result;
    }

    private Boolean checkRoomCode(String roomCode) {
        List<Room> allRooms = roomRepository.findAll();
        for (int i = 0; i < allRooms.size(); i++) {
            if(roomCode.equals(allRooms.get(i).getCode())) {
                return true;
            }
        }
        return false;
    }
}
