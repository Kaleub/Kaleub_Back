package com.kale.service;

import com.kale.domain.User;
import com.kale.dto.request.auth.CreateUserReqDto;
import com.kale.repository.UserRepository;
import com.kale.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceTest {

    @Autowired private AuthService authService;
    @Autowired private RedisUtil redisUtil;
    @Autowired private UserRepository userRepository;


    @Test
    public void 사용자생성() {

        //given
        String email = "heyazoo1007@gmail.com";
        String password = "12345";

        //이메일 인증번호 저장
        redisUtil.setDataExpire(email, "1", 60 * 60 * 24L);

        CreateUserReqDto dto = new CreateUserReqDto();
        dto.setEmail(email);
        dto.setPassword(password);

        //when
        authService.createUser(dto);
        Optional<User> findUser = userRepository.findByEmail(email);
        String testEmail = findUser.get().getEmail();

        //then
        assertThat(testEmail).isEqualTo(email);
    }
}