package com.photory.service;

import com.photory.controller.auth.dto.request.CreateUserRequestDto;
import com.photory.domain.user.User;
import com.photory.domain.user.repository.UserRepository;
import com.photory.common.util.RedisUtil;
import com.photory.service.auth.AuthService;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach


    @Test
    public void 이메일중복_확인() {

    }

    @Test
    public void 인증이메일_전송() {

    }

    @Test
    public void 인증이메일_완료() {

    }

    @Test
    public void createUser_() {

        //given
        String email = "heyazoo1007@gmail.com";
        String password = "12345";

        //이메일 인증번호 저장
        redisUtil.setDataExpire(email, "1", 60 * 60 * 24L);

        CreateUserRequestDto dto = CreateUserRequestDto.testBuilder()
                .email(email)
                .password(password)
                .build();

        //when
        authService.createUser(dto);
        Optional<User> findUser = userRepository.findByEmail(email);
        String testEmail = findUser.get().getEmail();

        //then
        assertThat(testEmail).isEqualTo(email);
    }
}