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

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUpUser(
            @RequestBody LoginUserReqDto loginFormDTO
            ) {

        User user = authService.signUpUser(
                loginFormDTO.getEmail(),
                loginFormDTO.getPassword()
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("회원가입 성공")
                        .data(user)
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
