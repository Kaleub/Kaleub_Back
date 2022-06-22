package com.photory.service;

import com.photory.constant.Role;
import com.photory.domain.Participate;
import com.photory.domain.Room;
import com.photory.domain.User;
import com.photory.dto.request.room.CreateRoomReqDto;
import com.photory.dto.request.room.DisableRoomReqDto;
import com.photory.dto.request.room.JoinRoomReqDto;
import com.photory.dto.request.room.LeaveRoomReqDto;
import com.photory.exception.*;
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

    @Test
    @DisplayName("joinRoomTest_실패_없는_방_코드")
    void joinRoomTest_실패_없는_방_코드() {
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

        JoinRoomReqDto joinRoomReqDto = JoinRoomReqDto.testBuilder()
                .code("없는 방 코드")
                .password("password1")
                .build();

        //when

        //then
        assertThrows(NotFoundRoomException.class, () -> roomService.joinRoom(notOwner.getEmail(), joinRoomReqDto));
    }

    @Test
    @DisplayName("joinRoomTest_실패_틀린_비밀번호")
    void joinRoomTest_실패_틀린_비밀번호() {
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
                .password("wrongpassword1")
                .build();

        //when

        //then
        assertThrows(InvalidPasswordException.class, () -> roomService.joinRoom(notOwner.getEmail(), joinRoomReqDto));
    }

    @Test
    @DisplayName("joinRoomTest_실패_최대_인원_초과")
    void joinRoomTest_실패_최대_인원_초과() {
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
        User user3 = User.builder()
                .email("user3@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user4 = User.builder()
                .email("user4@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user5 = User.builder()
                .email("user5@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user6 = User.builder()
                .email("user6@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user7 = User.builder()
                .email("user7@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user8 = User.builder()
                .email("user8@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User user9 = User.builder()
                .email("user9@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User roomOwner = userRepository.save(user1);
        User notOwner1 = userRepository.save(user2);
        User notOwner2 = userRepository.save(user3);
        User notOwner3 = userRepository.save(user4);
        User notOwner4 = userRepository.save(user5);
        User notOwner5 = userRepository.save(user6);
        User notOwner6 = userRepository.save(user7);
        User notOwner7 = userRepository.save(user8);
        User notOwner8 = userRepository.save(user9);

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
        roomService.joinRoom(notOwner1.getEmail(), joinRoomReqDto);
        roomService.joinRoom(notOwner2.getEmail(), joinRoomReqDto);
        roomService.joinRoom(notOwner3.getEmail(), joinRoomReqDto);
        roomService.joinRoom(notOwner4.getEmail(), joinRoomReqDto);
        roomService.joinRoom(notOwner5.getEmail(), joinRoomReqDto);
        roomService.joinRoom(notOwner6.getEmail(), joinRoomReqDto);
        roomService.joinRoom(notOwner7.getEmail(), joinRoomReqDto);

        //then
        assertThrows(ExceedRoomCapacityException.class, () -> roomService.joinRoom(notOwner8.getEmail(), joinRoomReqDto));
    }

    @Test
    @DisplayName("joinRoomTest_실패_이미_참가중인_방")
    void joinRoomTest_실패_이미_참가중인_방() {
        //given
        User user1 = User.builder()
                .email("user1@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User roomOwner = userRepository.save(user1);

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

        //then
        assertThrows(AlreadyInRoomException.class, () -> roomService.joinRoom(roomOwner.getEmail(), joinRoomReqDto));
    }

    @Test
    @DisplayName("leaveRoomTest_성공")
    void leaveRoomTest_성공() {
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

        roomService.joinRoom(notOwner.getEmail(), joinRoomReqDto);

        LeaveRoomReqDto leaveRoomReqDto = LeaveRoomReqDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when
        roomService.leaveRoom(notOwner.getEmail(), leaveRoomReqDto);

        //then
        Optional<Participate> participate = participateRepository.findByRoomAndUser(room.get(), notOwner);
        Optional<Room> leftRoom = roomRepository.findById(leaveRoomReqDto.getRoomId());

        assertAll(
                () -> assertTrue(participate.isEmpty()),
                () -> assertEquals(1, leftRoom.get().getParticipantsCount())
        );
    }

    @Test
    @DisplayName("leaveRoomTest_실패_참가중인_방이_아닌_경우")
    void leaveRoomTest_실패_참가중인_방이_아닌_경우() {
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

        LeaveRoomReqDto leaveRoomReqDto = LeaveRoomReqDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(AlreadyNotInRoomException.class, () -> roomService.leaveRoom(notOwner.getEmail(), leaveRoomReqDto));
    }

    @Test
    @DisplayName("leaveRoomTest_실패_방의_주인인데_다른_참여자가_남은_경우")
    void leaveRoomTest_실패_방의_주인인데_다른_참여자가_남은_경우() {
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

        roomService.joinRoom(notOwner.getEmail(), joinRoomReqDto);

        LeaveRoomReqDto leaveRoomReqDto = LeaveRoomReqDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(OwnerCanNotLeaveException.class, () -> roomService.leaveRoom(roomOwner.getEmail(), leaveRoomReqDto));
    }

    @Test
    @DisplayName("leaveRoomTest_실패_방의_주인인데_혼자_남은_경우")
    void leaveRoomTest_실패_방의_주인인데_혼자_남은_경우() {
        //given
        User user1 = User.builder()
                .email("user1@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User roomOwner = userRepository.save(user1);

        CreateRoomReqDto createRoomReqDto = CreateRoomReqDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomReqDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        LeaveRoomReqDto leaveRoomReqDto = LeaveRoomReqDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(AlertLeaveRoomException.class, () -> roomService.leaveRoom(roomOwner.getEmail(), leaveRoomReqDto));
    }

    @Test
    @DisplayName("disableRoomTest_성공")
    void disableRoomTest_성공() {
        //given
        User user1 = User.builder()
                .email("user1@gmail.com")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        User roomOwner = userRepository.save(user1);

        CreateRoomReqDto createRoomReqDto = CreateRoomReqDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomReqDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        DisableRoomReqDto disableRoomReqDto = DisableRoomReqDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when
        roomService.disableRoom(roomOwner.getEmail(), disableRoomReqDto);

        //then
        Optional<Room> disabledRoom = roomRepository.findByOwnerUser(roomOwner);

        assertAll(
                () -> assertEquals(false, disabledRoom.get().getStatus())
        );
    }
}
