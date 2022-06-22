package com.photory.service;

import com.photory.domain.User;
import com.photory.dto.request.auth.CreateUserReqDto;
import com.photory.repository.UserRepository;
import com.photory.util.RedisUtil;
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