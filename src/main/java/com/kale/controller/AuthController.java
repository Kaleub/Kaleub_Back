package com.kale.controller;

import com.kale.dto.ResponseDto;
import com.kale.dto.request.auth.LoginUserReqDto;
import com.kale.dto.response.auth.LoginUserResDto;
import com.kale.model.User;
import com.kale.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
  
    //이메일이 유효한거 확인 되면, 인증 버튼 누를 수 있음
    @PostMapping("/auth/signup/emailCheck")
    public ResponseEntity<ResponseDto> validateEmail(@Valid String email) {

        String message = authService.checkEmailDuplication(email);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message(message)
                        .data(null)
                        .build()
        );
    }

    //이메일 인증 실행
    @PostMapping("/auth/signup/email")
    public ResponseEntity<ResponseDto> authEmail(@RequestBody @Valid String email) {

        authService.authEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("이메일 전송 성공")
                        .data(null)
                        .build()
        );
    }

    //마지막으로 회원 생성
    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDto> createUser(@RequestBody SignUpReqDto signUpReqDto) {
        
        authService.createUser(signUpReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("회원가입 성공")
                       .data(null)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginUser(
            @RequestBody LoginUserReqDto loginUserReqDto
    ) {

        User user = authService.loginUser(
                loginUserReqDto.getEmail(),
                loginUserReqDto.getPassword()
        );

        String token = authService.createToken(user);

        LoginUserResDto loginUserResDto = LoginUserResDto.builder()
                .token(token)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("로그인 성공")
                        .data(loginUserResDto)
                        .build()
        );
    }

    @GetMapping("/check")
    ResponseEntity<ResponseDto> checkToken() {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("로그인 유지")
                        .data(null)
                        .build()
        );
    }
}
