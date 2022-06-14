package com.kale.controller;

import com.kale.dto.ResponseDto;
import com.kale.dto.request.LoginFormDto;
import com.kale.dto.request.SignUpReqDto;
import com.kale.model.User;
import com.kale.service.AuthService;
import com.kale.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDto> loginUser(
            @RequestBody LoginFormDto loginFormDto,
            HttpServletResponse response
    ) {

        User user = authService.loginUser(
                loginFormDto.getEmail(),
                loginFormDto.getPassword()
        );

        String token = authService.createToken(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("로그인 성공")
                        .data(token)
                        .build()
        );
    }

    //이메일이 유효한거 확인 되면, 인증 버튼 누를 수 있음
    @PostMapping("/auth/signup/emailCheck")
    public String validateEmail(@Valid String email) {

        String message=userService.checkEmailDuplication(email);
        return message;
    }

    //이메일 인증 실행
    @PostMapping("/auth/signup/email")
    public ResponseEntity<Void> authEmail(@RequestBody @Valid String email){

        userService.authEmail(email);
        return ResponseEntity.ok().build();
    }

    //마지막으로 회원 생성
    @PostMapping("auth/signup")
    public void createUser(SignUpReqDto signUpReqDto){
        userService.createUser(signUpReqDto);
    }

}
