package com.photory.service;

import com.photory.common.exception.model.NotFoundException;
import com.photory.common.exception.model.ValidationException;
import com.photory.common.util.JwtUtil;
import com.photory.controller.auth.dto.request.CreateUserRequestDto;
import com.photory.controller.auth.dto.request.SigninUserRequestDto;
import com.photory.domain.user.User;
import com.photory.domain.user.UserRole;
import com.photory.domain.user.repository.UserRepository;
import com.photory.common.util.RedisUtil;
import com.photory.service.auth.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAllInBatch();
    }


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

    @Test
    @DisplayName("signinUserTest_성공")
    public void signinUserTest_성공() {
        //given
        User user = User.of("user@gmail.com", passwordEncoder.encode("password123"), "닉네임", null, UserRole.ROLE_USER);
        userRepository.save(user);

        SigninUserRequestDto signinUserRequestDto = SigninUserRequestDto.testBuilder()
                .email("user@gmail.com")
                .password("password123")
                .build();

        //when
        String token = authService.signinUser(signinUserRequestDto);

        //then
        assertAll(
                () -> assertEquals("user@gmail.com", jwtUtil.getEmail(token)),
                () -> assertFalse(jwtUtil.isTokenExpired(token))
        );
    }

    @Test
    @DisplayName("signinUserTest_실패_아이디_틀린_경우")
    public void signinUserTest_실패_아이디_틀린_경우() {
        //given
        User user = User.of("user@gmail.com", passwordEncoder.encode("password123"), "닉네임", null, UserRole.ROLE_USER);
        userRepository.save(user);

        SigninUserRequestDto signinUserRequestDto = SigninUserRequestDto.testBuilder()
                .email("use@gmail.com")
                .password("password123")
                .build();

        //when

        //then
        assertThrows(NotFoundException.class, () -> authService.signinUser(signinUserRequestDto));
    }

    @Test
    @DisplayName("signinUserTest_실패_비밀번호_틀린_경우")
    public void signinUserTest_실패_비밀번호_틀린_경우() {
        //given
        User user = User.of("user@gmail.com", passwordEncoder.encode("password123"), "닉네임", null, UserRole.ROLE_USER);
        userRepository.save(user);

        SigninUserRequestDto signinUserRequestDto = SigninUserRequestDto.testBuilder()
                .email("user@gmail.com")
                .password("password12")
                .build();

        //when

        //then
        assertThrows(ValidationException.class, () -> authService.signinUser(signinUserRequestDto));
    }
}