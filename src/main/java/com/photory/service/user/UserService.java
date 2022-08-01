package com.photory.service.user;

import com.photory.common.exception.model.ConflictException;
import com.photory.controller.auth.dto.request.CreateUserRequestDto;
import com.photory.domain.user.User;
import com.photory.domain.user.UserRole;
import com.photory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.photory.common.exception.ErrorCode.CONFLICT_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
