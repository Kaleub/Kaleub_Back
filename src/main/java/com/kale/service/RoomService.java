package com.kale.service;

import com.kale.model.Participate;
import com.kale.model.Room;
import com.kale.repository.ParticipateRepository;
import com.kale.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipateRepository participateRepository;

    public Room createRoom(String userEmail, String title, String password) {

        Room room = Room.builder()
                .title(title)
                .password(password)
                .code(createRoomCode())
                .ownerEmail(userEmail)
                .build();

        Room created = roomRepository.save(room);

        Participate participate = Participate.builder()
                .room(created)
                .userEmail(userEmail)
                .build();

        participateRepository.save(participate);

        return created;
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
