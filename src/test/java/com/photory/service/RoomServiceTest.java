package com.photory.service;

import com.photory.common.exception.model.ConflictException;
import com.photory.common.exception.model.ForbiddenException;
import com.photory.common.exception.model.NotFoundException;
import com.photory.common.exception.model.ValidationException;
import com.photory.controller.room.dto.request.*;
import com.photory.controller.room.dto.response.GetRoomResponse;
import com.photory.domain.participate.Participate;
import com.photory.domain.participate.repository.ParticipateRepository;
import com.photory.domain.room.Room;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.User;
import com.photory.domain.user.UserRole;
import com.photory.domain.user.repository.UserRepository;
import com.photory.service.room.RoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        User user = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User saved = userRepository.save(user);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();

        //when
        roomService.createRoom(saved.getEmail(), createRoomRequestDto);

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
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        //when
        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

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
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code("없는 방 코드")
                .password("password1")
                .build();

        //when

        //then
        assertThrows(NotFoundException.class, () -> roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto));
    }

    @Test
    @DisplayName("joinRoomTest_실패_틀린_비밀번호")
    void joinRoomTest_실패_틀린_비밀번호() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("wrongpassword1")
                .build();

        //when

        //then
        assertThrows(ValidationException.class, () -> roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto));
    }

    @Test
    @DisplayName("joinRoomTest_실패_최대_인원_초과")
    void joinRoomTest_실패_최대_인원_초과() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user3 = User.of("user3@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user4 = User.of("user4@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user5 = User.of("user5@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user6 = User.of("user6@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user7 = User.of("user7@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user8 = User.of("user8@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user9 = User.of("user9@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);

        User roomOwner = userRepository.save(user1);
        User notOwner1 = userRepository.save(user2);
        User notOwner2 = userRepository.save(user3);
        User notOwner3 = userRepository.save(user4);
        User notOwner4 = userRepository.save(user5);
        User notOwner5 = userRepository.save(user6);
        User notOwner6 = userRepository.save(user7);
        User notOwner7 = userRepository.save(user8);
        User notOwner8 = userRepository.save(user9);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        //when
        roomService.joinRoom(notOwner1.getEmail(), joinRoomRequestDto);
        roomService.joinRoom(notOwner2.getEmail(), joinRoomRequestDto);
        roomService.joinRoom(notOwner3.getEmail(), joinRoomRequestDto);
        roomService.joinRoom(notOwner4.getEmail(), joinRoomRequestDto);
        roomService.joinRoom(notOwner5.getEmail(), joinRoomRequestDto);
        roomService.joinRoom(notOwner6.getEmail(), joinRoomRequestDto);
        roomService.joinRoom(notOwner7.getEmail(), joinRoomRequestDto);

        //then
        assertThrows(ForbiddenException.class, () -> roomService.joinRoom(notOwner8.getEmail(), joinRoomRequestDto));
    }

    @Test
    @DisplayName("joinRoomTest_실패_이미_참가중인_방")
    void joinRoomTest_실패_이미_참가중인_방() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> roomService.joinRoom(roomOwner.getEmail(), joinRoomRequestDto));
    }

    @Test
    @DisplayName("getRoomTest_성공")
    void getRoomTest_성공() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        //when
        GetRoomResponse getRoom1 = roomService.getRoom(roomOwner.getEmail(), room.get().getId());
        GetRoomResponse getRoom2 = roomService.getRoom(notOwner.getEmail(), room.get().getId());

        //then
        ArrayList<Participate> participates = participateRepository.findAllByRoom(room.get());

        assertAll(
                () -> assertEquals(room.get().getCode(), getRoom1.getCode()),
                () -> assertEquals(room.get().getOwnerUser().getEmail(), getRoom1.getOwnerEmail()),
                () -> assertEquals(room.get().getPassword(), getRoom1.getPassword()),
                () -> assertEquals(2, getRoom1.getParticipantsCount()),
                () -> assertEquals(2, getRoom1.getUserIds().size())
        );

        assertAll(
                () -> assertEquals(room.get().getCode(), getRoom2.getCode()),
                () -> assertEquals(room.get().getOwnerUser().getEmail(), getRoom2.getOwnerEmail()),
                () -> assertEquals(room.get().getPassword(), getRoom2.getPassword()),
                () -> assertEquals(2, getRoom2.getParticipantsCount()),
                () -> assertEquals(2, getRoom2.getUserIds().size())
        );

        assertThat(getRoom1.getUserIds()).containsExactly(participates.get(0).getId(), participates.get(1).getId());
        assertThat(getRoom2.getUserIds()).containsExactly(participates.get(0).getId(), participates.get(1).getId());
    }

    @Test
    @DisplayName("getRoom_실패_방_참가자가_아닌_경우")
    void getRoom_실패_방_참가자가_아닌_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        //then
        assertThrows(ForbiddenException.class, () -> roomService.getRoom(notOwner.getEmail(), room.get().getId()));
    }

    @Test
    @DisplayName("leaveRoomTest_성공")
    void leaveRoomTest_성공() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        LeaveRoomRequestDto leaveRoomRequestDto = LeaveRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when
        roomService.leaveRoom(notOwner.getEmail(), leaveRoomRequestDto);

        //then
        Optional<Participate> participate = participateRepository.findByRoomAndUser(room.get(), notOwner);
        Optional<Room> leftRoom = roomRepository.findById(leaveRoomRequestDto.getRoomId());

        assertAll(
                () -> assertTrue(participate.isEmpty()),
                () -> assertEquals(1, leftRoom.get().getParticipantsCount())
        );
    }

    @Test
    @DisplayName("leaveRoomTest_실패_참가중인_방이_아닌_경우")
    void leaveRoomTest_실패_참가중인_방이_아닌_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        LeaveRoomRequestDto leaveRoomRequestDto = LeaveRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> roomService.leaveRoom(notOwner.getEmail(), leaveRoomRequestDto));
    }

    @Test
    @DisplayName("leaveRoomTest_실패_방의_주인인데_다른_참여자가_남은_경우")
    void leaveRoomTest_실패_방의_주인인데_다른_참여자가_남은_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        LeaveRoomRequestDto leaveRoomRequestDto = LeaveRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.leaveRoom(roomOwner.getEmail(), leaveRoomRequestDto));
    }

    @Test
    @DisplayName("leaveRoomTest_실패_방의_주인인데_혼자_남은_경우")
    void leaveRoomTest_실패_방의_주인인데_혼자_남은_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        LeaveRoomRequestDto leaveRoomRequestDto = LeaveRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.leaveRoom(roomOwner.getEmail(), leaveRoomRequestDto));
    }

    @Test
    @DisplayName("disableRoomTest_성공")
    void disableRoomTest_성공() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        DisableRoomRequestDto disableRoomRequestDto = DisableRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when
        roomService.disableRoom(roomOwner.getEmail(), disableRoomRequestDto);

        //then
        Optional<Room> disabledRoom = roomRepository.findByOwnerUser(roomOwner);

        assertAll(
                () -> assertEquals(false, disabledRoom.get().getStatus())
        );
    }

    @Test
    @DisplayName("disableRoomTest_실패_방장이_아닌_경우")
    void disableRoomTest_실패_방장이_아닌_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        DisableRoomRequestDto disableRoomRequestDto = DisableRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.disableRoom(notOwner.getEmail(), disableRoomRequestDto));
    }

    @Test
    @DisplayName("disableRoomTest_실패_다른_참가자가_남은_경우")
    void disableRoomTest_실패_다른_참가자가_남은_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        DisableRoomRequestDto disableRoomRequestDto = DisableRoomRequestDto.testBuilder()
                .roomId(room.get().getId())
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.disableRoom(roomOwner.getEmail(), disableRoomRequestDto));
    }

    @Test
    @DisplayName("deleteUserForce_성공")
    void deleteUserForce_성공() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        DeleteUserForceRequestDto deleteUserForceRequestDto = DeleteUserForceRequestDto.testBuilder()
                .roomId(room.get().getId())
                .deletedUserId(notOwner.getId())
                .build();

        //when
        roomService.deleteUserForce(roomOwner.getEmail(), deleteUserForceRequestDto);

        //then
        Optional<Participate> participate = participateRepository.findByRoomAndUser(room.get(), notOwner);
        Optional<Room> leftRoom = roomRepository.findById(deleteUserForceRequestDto.getRoomId());

        assertAll(
                () -> assertTrue(participate.isEmpty()),
                () -> assertEquals(1, leftRoom.get().getParticipantsCount())
        );
    }

    @Test
    @DisplayName("deleteUserForce_실패_방장이_아닌_경우")
    void deleteUserForce_실패_방장이_아닌_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("제목")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        DeleteUserForceRequestDto deleteUserForceRequestDto = DeleteUserForceRequestDto.testBuilder()
                .roomId(room.get().getId())
                .deletedUserId(notOwner.getId())
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.deleteUserForce(notOwner.getEmail(), deleteUserForceRequestDto));
    }

    @Test
    @DisplayName("deleteUserForce_실패_방에_없는_사용자는_강퇴_불가능한_경우")
    void deleteUserForce_실패_방에_없는_사용자는_강퇴_불가능한_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notInRoom = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("제목")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        DeleteUserForceRequestDto deleteUserForceRequestDto = DeleteUserForceRequestDto.testBuilder()
                .roomId(room.get().getId())
                .deletedUserId(notInRoom.getId())
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> roomService.deleteUserForce(roomOwner.getEmail(), deleteUserForceRequestDto));
    }

    @Test
    @DisplayName("modifyRoomPasswordTest_성공")
    void modifyRoomPasswordTest_성공() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        ModifyRoomPasswordRequestDto modifyRoomPasswordRequestDto = ModifyRoomPasswordRequestDto.testBuilder()
                .roomId(room.get().getId())
                .afterPassword("password2")
                .build();

        //when
        roomService.modifyRoomPassword(roomOwner.getEmail(), modifyRoomPasswordRequestDto);

        //then
        Optional<Room> modifiedRoom = roomRepository.findByOwnerUser(roomOwner);

        assertAll(
                () -> assertEquals("password2", modifiedRoom.get().getPassword())
        );
    }

    @Test
    @DisplayName("modifyRoomPasswordTest_실패_방장이_아닌_경우")
    void modifyRoomPasswordTest_실패_방장이_아닌_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        ModifyRoomPasswordRequestDto modifyRoomPasswordRequestDto = ModifyRoomPasswordRequestDto.testBuilder()
                .roomId(room.get().getId())
                .afterPassword("password2")
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.modifyRoomPassword(notOwner.getEmail(), modifyRoomPasswordRequestDto));
    }

    @Test
    @DisplayName("delegateOwner_성공")
    void delegateOwner_성공() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("제목")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();
        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        DelegateOwnerRequestDto delegateOwnerRequestDto = DelegateOwnerRequestDto.testBuilder()
                .roomId(room.get().getId())
                .delegatedUserId(notOwner.getId())
                .build();

        //when
        roomService.delegateOwner(roomOwner.getEmail(), delegateOwnerRequestDto);

        //then
        Optional<Room> modifiedRoom = roomRepository.findByOwnerUser(notOwner);

        assertThat(modifiedRoom.get().getId()).isEqualTo(room.get().getId());
    }

    @Test
    @DisplayName("delegateOwner_실패_방장이_아닐때_위임_불가능한_경우")
    void delegateOwner_실패_방장이_아닐때_위임_불가능한_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("제목")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        DelegateOwnerRequestDto delegateOwnerRequestDto = DelegateOwnerRequestDto.testBuilder()
                .roomId(room.get().getId())
                .delegatedUserId(roomOwner.getId())
                .build();

        //when

        //then
        assertThrows(ForbiddenException.class, () -> roomService.delegateOwner(notOwner.getEmail(), delegateOwnerRequestDto));
    }

    @Test
    @DisplayName("delegateOwner_실패_방에_없는_사람_위임_불가능한_경우")
    void delegateOwner_실패_방에_없는_사람_위임_불가능한_경우() {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User roomOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("제목")
                .password("password1")
                .build();
        roomService.createRoom(roomOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(roomOwner);

        DelegateOwnerRequestDto delegateOwnerRequestDto = DelegateOwnerRequestDto.testBuilder()
                .roomId(room.get().getId())
                .delegatedUserId(notOwner.getId())
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> roomService.delegateOwner(roomOwner.getEmail(), delegateOwnerRequestDto));
    }
}
