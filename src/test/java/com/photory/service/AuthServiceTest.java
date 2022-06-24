package com.photory.service;

import com.photory.common.exception.model.ConflictException;
import com.photory.common.exception.model.NotFoundException;
import com.photory.common.exception.model.ValidationException;
import com.photory.common.util.JwtUtil;
import com.photory.controller.auth.dto.request.*;
import com.photory.controller.auth.dto.request.ValidateEmailRequestDto.ValidateEmailRequestDtoBuilder;
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
    @DisplayName("validateEmail_성공")
    public void validateEmail_성공() {
        //given
        String email = "test@gmail.com";
        ValidateEmailRequestDto dto = ValidateEmailRequestDto.testBuilder()
                .email(email)
                .build();

        //when
        authService.validateEmail(dto);
        Optional<User> user = userRepository.findByEmail(email);

        //then
        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("validateEmail_실패_중복이메일_입력한_경우")
    public void validateEmail_실패_중복이메일_입력한_경우() {
        //given
        String email = "test@gmail.com";
        User user = User.of(email, "1234", "kim", "imageUrl", UserRole.ROLE_USER);
        userRepository.save(user);
        ValidateEmailRequestDto dto = ValidateEmailRequestDto.testBuilder()
                .email(email)
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> authService.validateEmail(dto));
    }

    @Test
    @DisplayName("authEmail_실패_중복이메일_입력한_경우")
    public void authEmail_실패_중복이메일_입력한_경우() {
        //given
        String email = "test@gmail.com";
        User user = User.of(email, "1234", "kim", "imageUrl", UserRole.ROLE_USER);
        userRepository.save(user);

        AuthEmailRequestDto dto = AuthEmailRequestDto.testBuilder()
                .email(email)
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> authService.authEmail(dto));
    }

    @Test
    @DisplayName("authEmailComplete_성공")
    public void authEmailComplete_성공() {
        //given
        String authKey = "123456";
        String email = "test@gmail.com";
        redisUtil.setDataExpire(authKey, email, 60 * 3L);

        AuthEmailCompleteRequestDto dto = AuthEmailCompleteRequestDto.testBuilder()
                .authKey(authKey)
                .email(email)
                .build();

        //when
        authService.authEmailComplete(dto);
        String findEmail = redisUtil.getData(authKey);

        //then
        assertThat(findEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("authEmailComplete_실패_틀린_인증번호_입력한_경우")
    public void authEmailComplete_실패_틀린_인증번호_입력한_경우() {
        //given
        String authKey = "123456";
        String wrongAuthKey = "123457";
        String email = "test@gmail.com";
        redisUtil.setDataExpire(authKey, email, 60 * 3L);

        AuthEmailCompleteRequestDto dto = AuthEmailCompleteRequestDto.testBuilder()
                .authKey(wrongAuthKey)
                .email(email)
                .build();

        //when

        //then
        assertThrows(ValidationException.class, () -> authService.authEmailComplete(dto));
    }

    @Test
    @DisplayName("createUser_성공")
    public void createUser_성공() {
        //given
        String email = "heyazoo1007@gmail.com";
        String password = "12345";
        String nickName = "nickname";

        //이메일 인증번호 저장
        redisUtil.setDataExpire(email, "1", 60 * 60 * 24L);

        CreateUserRequestDto dto = CreateUserRequestDto.testBuilder()
                .email(email)
                .password(password)
                .nickname(nickName)
                .build();

        //when
        authService.createUser(dto);
        Optional<User> findUser = userRepository.findByEmail(email);
        String testEmail = findUser.get().getEmail();

        //then
        assertThat(testEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("createUser_실패_중복으로_회원가입한_경우")
    public void createUser_실패_중복으로_회원가입한_경우() {
        //given
        String email = "test@gmail.com";
        String password = "password1234";
        String nickName = "nickname";
        User user = User.of(email, passwordEncoder.encode(password), nickName, null, UserRole.ROLE_USER);

        userRepository.save(user);

        CreateUserRequestDto dto = CreateUserRequestDto.testBuilder()
                .email(email)
                .password(password)
                .nickname(nickName)
                .build();

        //when

        //then
        assertThrows(ConflictException.class, () -> authService.createUser(dto));
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