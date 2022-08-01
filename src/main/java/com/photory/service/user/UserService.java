package com.photory.service.user;

import com.photory.common.exception.model.ConflictException;
import com.photory.common.util.RedisUtil;
import com.photory.controller.auth.dto.request.CreateUserRequestDto;
import com.photory.domain.participate.Participate;
import com.photory.domain.participate.repository.ParticipateRepository;
import com.photory.domain.room.Room;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.User;
import com.photory.domain.user.UserRole;
import com.photory.domain.user.UserStatus;
import com.photory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.photory.common.exception.ErrorCode.CONFLICT_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ParticipateRepository participateRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    public void createUser(CreateUserRequestDto request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("이미 가입된 유저의 이메일 (%s) 입니다.", email), CONFLICT_USER_EXCEPTION);
        }

//       if (redisUtil.getData(email) != null && redisUtil.getData(email).compareTo("1") == 0) {
        User user = User.of(email, passwordEncoder.encode(password), nickname, null, UserRole.ROLE_USER);

        userRepository.save(user);
//        } else {
//            throw new UnAuthorizedException(String.format("인증이 완료되지 않은 이메일 (%s) 입니다.", email), UNAUTHORIZED_EMAIL_EXCEPTION);
//        }
    }

    //TODO 탈퇴된 사용자, 비활성화된 방 일정 기간 후 데이터 삭제
    public void deleteUser(String userEmail) {
        User user = UserServiceUtils.findUserByEmail(userRepository, userEmail);

        ArrayList<Participate> participatingList = participateRepository.findAllByUser(user);

        // 방장이 아닌 방 전부 나가기
        // 방장이었던 방에 인원이 남은 경우 방장 위임 후 나가거나 혼자 남았을 경우 방 비활성화
        for (Participate participating : participatingList) {
            Room participatingRoom = participating.getRoom();
            User ownerUser = participatingRoom.getOwnerUser();
            ArrayList<Participate> participatingListWithOthers = participateRepository.findAllByRoom(participatingRoom);

            // 사용자가 방장이 아니라면 방을 나감
            if (user.getId() != ownerUser.getId()) {
                participateRepository.delete(participating);

                participatingRoom.setParticipantsCount(participatingRoom.getParticipantsCount() - 1);
                roomRepository.save(participatingRoom);
            }

            // 사용자가 방의 주인인데 다른 참여자가 남아 있다면 방장 위임 후 방을 나감
            else if (user.getId() == ownerUser.getId() && participatingListWithOthers.size() > 1) {
                for (Participate participatingOther : participatingListWithOthers) {
                    if (user.getId() != participatingOther.getUser().getId()) {
                        participatingRoom.setOwnerUser(participatingOther.getUser());

                        participateRepository.delete(participating);

                        participatingRoom.setParticipantsCount(participatingRoom.getParticipantsCount() - 1);
                        roomRepository.save(participatingRoom);
                        break;
                    }
                }
            }

            // 사용자가 방의 주인이고 방에 혼자 남아 있다면 방을 비활성화함
            else if (user.getId() == ownerUser.getId() && participatingListWithOthers.size() == 1) {
                participatingRoom.setStatus(false);
                roomRepository.save(participatingRoom);
            }
        }

        // 회원 탈퇴
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        redisUtil.deleteData(user.getEmail());
    }
}
