package com.photory.service.room;

import com.photory.common.exception.model.ConflictException;
import com.photory.common.exception.model.ForbiddenException;
import com.photory.common.exception.model.NotFoundException;
import com.photory.common.exception.model.ValidationException;
import com.photory.controller.room.dto.request.*;
import com.photory.controller.room.dto.response.CreateRoomResponse;
import com.photory.controller.room.dto.response.GetRoomsResponse;
import com.photory.controller.room.dto.response.JoinRoomResponse;
import com.photory.domain.participate.Participate;
import com.photory.domain.room.Room;
import com.photory.domain.user.User;
import com.photory.domain.participate.repository.ParticipateRepository;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.photory.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ParticipateRepository participateRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateRoomResponse createRoom(String userEmail, CreateRoomRequestDto request) {
        String title = request.getTitle();
        String password = request.getPassword();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);

        Room room = Room.of(createRoomCode(), user, title, passwordEncoder.encode(password), 1, true);

        Room created = roomRepository.save(room);

        Participate participate = Participate.of(created, user);

        participateRepository.save(participate);

        CreateRoomResponse response = CreateRoomResponse.of(created);

        return response;
    }

    public JoinRoomResponse joinRoom(String userEmail, JoinRoomRequestDto request) {
        String code = request.getCode();
        String password = request.getPassword();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);

        Optional<Room> room = roomRepository.findByCode(code);

        if (room.isPresent()) {
            if (passwordEncoder.matches(password, room.get().getPassword())) {
                if (room.get().getParticipantsCount() >= 8) {
                    throw new ForbiddenException(String.format("방 (%s) 은 최대 인원 8명을 넘을 수 없습니다.", room.get().getId()), FORBIDDEN_ROOM_EXCEED_CAPACITY_EXCEPTION);
                }
                Optional<Participate> participating = participateRepository.findByRoomAndUser(room.get(), user);
                if (participating.isPresent()) {
                    throw new ConflictException(String.format("유저 (%s) 는 이미 방 (%s) 에 참가중입니다.", user.getId(), room.get().getId()), CONFLICT_JOIN_ROOM_EXCEPTION);
                } else {
                    Participate participate = Participate.of(room.get(), user);

                    participateRepository.save(participate);

                    room.get().setParticipantsCount(room.get().getParticipantsCount() + 1);
                    roomRepository.save(room.get());

                    JoinRoomResponse response = JoinRoomResponse.of(room.get());

                    return response;
                }
            } else {
                throw new ValidationException("잘못된 비밀번호입니다.", VALIDATION_WRONG_PASSWORD_EXCEPTION);
            }
        } else {
            throw new NotFoundException(String.format("존재하지 않는 방 (%s) 입니다", code), NOT_FOUND_ROOM_EXCEPTION);
        }
    }

    public ArrayList<GetRoomsResponse> getRooms(String userEmail) {
        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);

        ArrayList<Room> rooms = new ArrayList<>();

        ArrayList<Participate> participates = participateRepository.findAllByUser(user);
        for (int i = 0; i < participates.size(); i++) {
            rooms.add(participates.get(i).getRoom());
        }

        ArrayList<GetRoomsResponse> response = new ArrayList<>();
        rooms.forEach((room -> {
            GetRoomsResponse getRoomsResponse = GetRoomsResponse.of(room);

            response.add(getRoomsResponse);
        }));

        return response;
    }

    public void leaveRoom(String userEmail, LeaveRoomRequestDto request) {
        Long roomId = request.getRoomId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        Optional<Participate> participate = participateRepository.findByRoomAndUser(room, user);

        if (participate.isPresent()) {

            User ownerUser = room.getOwnerUser();
            ArrayList<Participate> participateArrayList = participateRepository.findAllByRoom(room);

            // 사용자가 방의 주인인데 다른 참여자가 남아 있다면 방을 나갈 수 없음
            if (user.getId() == ownerUser.getId() && participateArrayList.size() > 1) {
                throw new ForbiddenException(String.format("(%s) 방의 방장 (%s) 는 다른 참여자가 남아 있다면 방을 나갈 수 없습니다.", room.getId(), user.getId()), FORBIDDEN_ROOM_OWNER_LEAVE_LAST_EXCEPTION);
            }

            // 사용자가 방의 주인이고 방에 혼자 남아 있다면 방을 나갈 수 없고 비활성화 할 수 있다는 메시지를 보냄
            if (user.getId() == ownerUser.getId() && participateArrayList.size() == 1) {
                throw new ForbiddenException(String.format("(%s) 방의 방장 (%s) 은 방을 비활성화 할 수 있습니다.", room.getId(), user.getId()), FORBIDDEN_ROOM_OWNER_LEAVE_EXCEPTION);
            }

            // 사용자가 방의 주인이 아니면 방을 나감
            participateRepository.delete(participate.get());

            room.setParticipantsCount(room.getParticipantsCount() - 1);
            roomRepository.save(room);
        } else {
            throw new ConflictException(String.format("유저 (%s) 는 이미 방 (%s) 을 나갔습니다.", user.getId(), room.getId()), CONFLICT_LEAVE_ROOM_EXCEPTION);
        }
    }

    public void disableRoom(String userEmail, DisableRoomRequestDto request) {
        Long roomId = request.getRoomId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        // 방장이 아니면 방을 비활성화할 수 없음
        if (user.getId() != ownerUser.getId()) {
            throw new ForbiddenException(String.format("해당 유저 (%s) 는 방장이 아닙니다.", user.getId()), FORBIDDEN_ROOM_OWNER_EXCEPTION);
        }

        // 방장을 제외한 다른 참가자가 더 있으면 방을 비활성화할 수 없음
        ArrayList<Participate> participates = participateRepository.findAllByRoom(room);
        if (participates.size() > 1) {
            throw new ForbiddenException(String.format("(%s) 방의 방장 (%s) 는 다른 참여자가 남아 있다면 방을 비활성화 시킬 수 없습니다.", room.getId(), user.getId()), FORBIDDEN_ROOM_OWNER_DISABLE_LAST_EXCEPTION);
        }

        // 방 비활성화
        room.setStatus(false);
        roomRepository.save(room);
    }

    public void deleteUserForce(String userEmail, DeleteUserForceRequestDto request) {
        Long deletedUserId = request.getDeletedUserId();
        Long roomId = request.getRoomId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        User deletedUser = RoomServiceUtils.findUserById(userRepository, deletedUserId);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        //방장이 아니면 사용자 강퇴시킬 수 없음
        if (user.getId() != ownerUser.getId()) {
            throw new ForbiddenException(String.format("해당 유저 (%s) 는 방장이 아닙니다.", user.getId()), FORBIDDEN_ROOM_OWNER_EXCEPTION);
        }

        //방에 참가하지 않은 사용자 강퇴시킬 수 없음
        Optional<Participate> participatingUser = participateRepository.findByRoomAndUser(room, deletedUser);
        if (participatingUser.isEmpty()) {
            throw new ConflictException(String.format("유저 (%s) 는 이미 방 (%s) 을 나갔습니다.", deletedUser.getId(), room.getId()), CONFLICT_LEAVE_ROOM_EXCEPTION);
        }

        participateRepository.delete(participatingUser.get());
        room.setParticipantsCount(room.getParticipantsCount() - 1);
        roomRepository.save(room);
    }

    public void modifyRoomPassword(String userEmail, ModifyRoomPasswordRequestDto request) {
        Long roomId = request.getRoomId();
        String beforePassword = request.getBeforePassword();
        String afterPassword = request.getAfterPassword();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        // 방장이 아니면 비밀번호를 변경할 수 없음
        if (user.getId() != ownerUser.getId()) {
            throw new ForbiddenException(String.format("해당 유저 (%s) 는 방장이 아닙니다.", user.getId()), FORBIDDEN_ROOM_OWNER_EXCEPTION);
        }

        // 이전 비밀번호가 틀리면 비밀번호를 변경할 수 없음
        if (!passwordEncoder.matches(beforePassword, room.getPassword())) {
            throw new ValidationException("잘못된 비밀번호입니다.", VALIDATION_WRONG_PASSWORD_EXCEPTION);
        }

        room.setPassword(passwordEncoder.encode(afterPassword));

        roomRepository.save(room);
    }

    public void delegateOwner(String userEmail, DelegateOwnerRequestDto request) {
        Long roomId = request.getRoomId();
        Long delegatedUserId = request.getDelegatedUserId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        User delegatedUser = RoomServiceUtils.findUserById(userRepository, delegatedUserId);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        // 방장이 아니면 방장 변경 불가능
        if (user.getId() != ownerUser.getId()) {
            throw new ForbiddenException(String.format("해당 유저 (%s) 는 방장이 아닙니다.", user.getId()), FORBIDDEN_ROOM_OWNER_EXCEPTION);
        }

        //위임하려는 사용자가 방에 없으면 위임 불가
        Optional<Participate> participatingUser = participateRepository.findByRoomAndUser(room, delegatedUser);
        if (participatingUser.isEmpty()) {
            throw new ConflictException(String.format("유저 (%s) 는 이미 방 (%s) 을 나갔습니다.", delegatedUser.getId(), room.getId()), CONFLICT_LEAVE_ROOM_EXCEPTION);
        }

        room.setOwnerUser(delegatedUser);

        roomRepository.save(room);
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
