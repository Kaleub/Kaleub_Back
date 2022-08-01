package com.photory.service.user;

import com.photory.common.exception.model.UnAuthorizedException;
import com.photory.domain.user.User;
import com.photory.domain.user.repository.UserRepository;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public class UserServiceUtils {

    public static User findUserByEmail(UserRepository userRepository, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new UnAuthorizedException("로그인 오류입니다.");
        }

        return user.get();
    }
}
