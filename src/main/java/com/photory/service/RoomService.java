package com.photory.service;

import com.photory.dto.request.room.*;
import com.photory.dto.response.room.CreateRoomResDto;
import com.photory.dto.response.room.GetRoomsResDto;
import com.photory.dto.response.room.JoinRoomResDto;
import com.photory.exception.*;
import com.photory.domain.Participate;
import com.photory.domain.Room;
import com.photory.domain.User;
import com.photory.repository.ParticipateRepository;
import com.photory.repository.RoomRepository;
import com.photory.repository.UserRepository;
import com.photory.util.DateUtil;
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

    public CreateRoomResDto createRoom(String userEmail, CreateRoomReqDto createRoomReqDto) {
        String title = createRoomReqDto.getTitle();
        String password = createRoomReqDto.getPassword();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);

        Room room = Room.of(createRoomCode(), user, title, passwordEncoder.encode(password), 1, true);

        Room created = roomRepository.save(room);

        Participate participate = Participate.of(created, user);

        participateRepository.save(participate);

        CreateRoomResDto createRoomResDto = CreateRoomResDto.of(created.getId(),
                created.getCode(),
                created.getOwnerUser().getEmail(),
                created.getTitle(),
                created.getPassword(),
                created.getParticipantsCount(),
                created.getStatus(),
                DateUtil.convertToTimeInterval(room.getCreatedDate()),
                DateUtil.convertToTimeInterval(room.getModifiedDate()));

        return createRoomResDto;
    }

    public JoinRoomResDto joinRoom(String userEmail, JoinRoomReqDto joinRoomReqDto) {
        String code = joinRoomReqDto.getCode();
        String password = joinRoomReqDto.getPassword();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);

        Optional<Room> room = roomRepository.findByCode(code);

        if (room.isPresent()) {
            if (passwordEncoder.matches(password, room.get().getPassword())) {
                if (room.get().getParticipantsCount() >= 8) {
                    throw new ExceedRoomCapacityException();
                }
                Optional<Participate> participating = participateRepository.findByRoomAndUser(room.get(), user);
                if (participating.isPresent()) {
                    throw new AlreadyInRoomException();
                } else {
                    Participate participate = Participate.of(room.get(), user);

                    participateRepository.save(participate);

                    room.get().setParticipantsCount(room.get().getParticipantsCount() + 1);
                    roomRepository.save(room.get());

                    JoinRoomResDto joinRoomResDto = JoinRoomResDto.of(room.get().getId(),
                            room.get().getCode(),
                            room.get().getOwnerUser().getEmail(),
                            room.get().getTitle(),
                            room.get().getPassword(),
                            room.get().getParticipantsCount(),
                            room.get().getStatus(),
                            DateUtil.convertToTimeInterval(room.get().getCreatedDate()),
                            DateUtil.convertToTimeInterval(room.get().getModifiedDate()));

                    return joinRoomResDto;
                }
            } else {
                throw new InvalidPasswordException();
            }
        } else {
            throw new NotFoundRoomException();
        }
    }

    public ArrayList<GetRoomsResDto> getRooms(String userEmail) {
        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);

        ArrayList<Room> rooms = new ArrayList<>();

        ArrayList<Participate> participates = participateRepository.findAllByUser(user);
        for (int i = 0; i < participates.size(); i++) {
            rooms.add(participates.get(i).getRoom());
        }

        ArrayList<GetRoomsResDto> getRoomsResDtos = new ArrayList<>();
        rooms.forEach((room -> {
            GetRoomsResDto getRoomsResDto = GetRoomsResDto.of(room.getId(),
                    room.getCode(),
                    room.getOwnerUser().getEmail(),
                    room.getTitle(),
                    room.getPassword(),
                    room.getParticipantsCount(),
                    room.getStatus(),
                    DateUtil.convertToTimeInterval(room.getCreatedDate()),
                    DateUtil.convertToTimeInterval(room.getModifiedDate()));

            getRoomsResDtos.add(getRoomsResDto);
        }));

        return getRoomsResDtos;
    }

    public void leaveRoom(String userEmail, LeaveRoomReqDto leaveRoomReqDto) {
        Long roomId = leaveRoomReqDto.getRoomId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        Optional<Participate> participate = participateRepository.findByRoomAndUser(room, user);

        if (participate.isPresent()) {

            User ownerUser = room.getOwnerUser();
            ArrayList<Participate> participateArrayList = participateRepository.findAllByRoom(room);

            // 사용자가 방의 주인인데 다른 참여자가 남아 있다면 방을 나갈 수 없음
            if (user.getId() == ownerUser.getId() && participateArrayList.size() > 1) {
                throw new OwnerCanNotLeaveException();
            }

            // 사용자가 방의 주인이고 방에 혼자 남아 있다면 방을 나갈 수 없고 비활성화 할 수 있다는 메시지를 보냄
            if (user.getId() == ownerUser.getId() && participateArrayList.size() == 1) {
                throw new AlertLeaveRoomException();
            }

            // 사용자가 방의 주인이 아니면 방을 나감
            participateRepository.delete(participate.get());

            room.setParticipantsCount(room.getParticipantsCount() - 1);
            roomRepository.save(room);
        } else {
            throw new AlreadyNotInRoomException();
        }
    }

    public void disableRoom(String userEmail, DisableRoomReqDto disableRoomReqDto) {
        Long roomId = disableRoomReqDto.getRoomId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        // 방장이 아니면 방을 비활성화할 수 없음
        if (user.getId() != ownerUser.getId()) {
            throw new NotOwnerException();
        }

        // 방장을 제외한 다른 참가자가 더 있으면 방을 비활성화할 수 없음
        ArrayList<Participate> participates = participateRepository.findAllByRoom(room);
        if (participates.size() > 1) {
            throw new NotAloneException();
        }

        // 방 비활성화
        room.setStatus(false);
        roomRepository.save(room);
    }

    public void deleteUserForce(String userEmail, DeleteUserForceReqDto deleteUserForceReqDto) {
        Long deletedUserId = deleteUserForceReqDto.getDeletedUserId();
        Long roomId = deleteUserForceReqDto.getRoomId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Optional<User> deletedUser = userRepository.findById(deletedUserId);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        //방장이 아니면 사용자 강퇴시킬 수 없음
        if (user.getId() != ownerUser.getId()) {
            throw new NotOwnerException();
        }

        //참가 방이 아니면 강퇴시킬 수 없음
        Optional<Participate> participatingOwner = participateRepository.findByRoomAndUser(room, user);
        if (participatingOwner.isEmpty()) {
            throw new AlreadyNotInRoomException();
        }

        //방에 참가하지 않은 사용자 강퇴시킬 수 없음
        Optional<Participate> participatingUser = participateRepository.findByRoomAndUser(room, deletedUser.get());
        if (participatingUser.isEmpty()) {
            throw new UserAlreadyNotInRoomException();
        }

        participateRepository.delete(participatingUser.get());
        room.setParticipantsCount(room.getParticipantsCount() - 1);
        roomRepository.save(room);
    }

    public void modifyRoomPassword(String userEmail, ModifyRoomPasswordReqDto modifyRoomPasswordReqDto) {
        Long roomId = modifyRoomPasswordReqDto.getRoomId();
        String beforePassword = modifyRoomPasswordReqDto.getBeforePassword();
        String afterPassword = modifyRoomPasswordReqDto.getAfterPassword();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        // 방장이 아니면 비밀번호를 변경할 수 없음
        if (user.getId() != ownerUser.getId()) {
            throw new NotOwnerException();
        }

        // 이전 비밀번호가 틀리면 비밀번호를 변경할 수 없음
        if (!passwordEncoder.matches(beforePassword, room.getPassword())) {
            throw new InvalidPasswordException();
        }

        room.setPassword(passwordEncoder.encode(afterPassword));

        roomRepository.save(room);
    }

    public void delegateOwner(String userEmail, DelegateOwnerReqDto delegateOwnerReqDto) {
        Long roomId = delegateOwnerReqDto.getRoomId();
        Long delegatedUserId = delegateOwnerReqDto.getDelegatedUserId();

        User user = RoomServiceUtils.findUserByEmail(userRepository, userEmail);
        Optional<User> delegatedUser = userRepository.findById(delegatedUserId);
        Room room = RoomServiceUtils.findRoomByRoomId(roomRepository, roomId);

        User ownerUser = room.getOwnerUser();

        // 방장이 아니면 방장 변경 불가능
        if (user.getId() != ownerUser.getId()) {
            throw new NotOwnerException();
        }

        //참가한 방이 아니면 위임 불가능
        Optional<Participate> participatingOwner = participateRepository.findByRoomAndUser(room, user);
        if (participatingOwner.isEmpty()) {
            throw new AlreadyNotInRoomException();
        }

        //위임하려는 사용자가 방에 없으면 위임 불가
        Optional<Participate> participatingUser = participateRepository.findByRoomAndUser(room, delegatedUser.get());
        if (participatingUser.isEmpty()) {
            throw new UserAlreadyNotInRoomException();
        }

        room.setOwnerUser(delegatedUser.get());

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
