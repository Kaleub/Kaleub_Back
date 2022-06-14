package com.kale.controller;

import com.kale.dto.ResponseDto;
import com.kale.dto.request.LoginFormDto;
import com.kale.model.User;
import com.kale.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDto> signUpUser(
            @RequestBody LoginFormDto loginFormDTO
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
}
