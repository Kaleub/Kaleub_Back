package com.kale.service;

import com.kale.constant.Role;
import com.kale.domain.Participate;
import com.kale.domain.Room;
import com.kale.domain.User;
import com.kale.dto.request.room.CreateRoomReqDto;
import com.kale.repository.ParticipateRepository;
import com.kale.repository.RoomRepository;
import com.kale.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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
                () -> assertEquals(saved.getEmail(), participate.get().getUser().getEmail()),
                () -> assertEquals(saved.getPassword(), participate.get().getUser().getPassword()),
                () -> assertEquals(saved.getRole(), participate.get().getUser().getRole()),
                () -> assertEquals(room.get().getId(), participate.get().getRoom().getId()),
                () -> assertEquals(room.get().getTitle(), participate.get().getRoom().getTitle()),
                () -> assertEquals(room.get().getPassword(), participate.get().getRoom().getPassword()),
                () -> assertEquals(room.get().getOwnerUser().getId(), participate.get().getRoom().getOwnerUser().getId())
        );
    }
}
