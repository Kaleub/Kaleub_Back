package com.photory.service;

import com.photory.constant.Role;
import com.photory.domain.Participate;
import com.photory.domain.Room;
import com.photory.domain.User;
import com.photory.dto.request.room.CreateRoomReqDto;
import com.photory.dto.request.room.JoinRoomReqDto;
import com.photory.repository.ParticipateRepository;
import com.photory.repository.RoomRepository;
import com.photory.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipateRepository participateRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        participateRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("createRoomTest_성공")
    void createRoomTest_성공() {
        //given
        User user = User.builder()
                .email("user1@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User saved = userRepository.save(user);

        CreateRoomReqDto createRoomReqDto = CreateRoomReqDto.testBuilder()
                .title("room")
                .password("password1")
                .build();

        //when
        roomService.createRoom(saved.getEmail(), createRoomReqDto);

        //then
        Optional<Room> room = roomRepository.findByOwnerUser(saved);
        Optional<Participate> participate = participateRepository.findByRoomAndUser(room.get(), saved);

        assertAll(
                () -> assertTrue(room.isPresent()),
                () -> assertTrue(participate.isPresent()),
                () -> assertEquals(saved.getId(), participate.get().getUser().getId()),
                () -> assertEquals(room.get().getId(), participate.get().getRoom().getId()),
                () -> assertEquals(room.get().getOwnerUser().getId(), participate.get().getRoom().getOwnerUser().getId())
        );
    }

    @Test
    @DisplayName("joinRoomTest_성공")
    void joinRoomTest_성공() {
        //given
        User user1 = User.builder()
                .email("user1@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user2 = User.builder()
                .email("user2@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomReqDto createRoomReqDto = CreateRoomReqDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomReqDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomReqDto joinRoomReqDto = JoinRoomReqDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        //when
        roomService.joinRoom(notOwner.getEmail(), joinRoomReqDto);

        //then
        Optional<Room> joinedRoom = roomRepository.findByOwnerUser(roomOwner);
        Optional<Participate> participate = participateRepository.findByRoomAndUser(joinedRoom.get(), notOwner);

        assertAll(
                () -> assertTrue(participate.isPresent()),
                () -> assertEquals(2, joinedRoom.get().getParticipantsCount()),
                () -> assertEquals(participate.get().getRoom().getId(), joinedRoom.get().getId())
        );
    }
}
