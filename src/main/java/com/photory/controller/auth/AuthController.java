package com.photory.controller.auth;

import com.photory.common.dto.ApiResponse;
import com.photory.controller.auth.dto.request.*;
import com.photory.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
  
    //이메일이 유효한거 확인 되면, 인증 버튼 누를 수 있음
    @PostMapping("/signup/email/check")
    public ApiResponse<String> validateEmail(@RequestBody @Valid ValidateEmailRequestDto request) {
        authService.validateEmail(request);
        return ApiResponse.SUCCESS;
    }

    //이메일 인증 실행
    @PostMapping("/signup/email")
    public ApiResponse<String> authEmail(@RequestBody @Valid AuthEmailRequestDto request) {
        authService.authEmail(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/signup/email/complete")
    public ApiResponse<String> authEmailComplete(@RequestBody @Valid AuthEmailCompleteRequestDto request) {
        authService.authEmailComplete(request);
        return ApiResponse.SUCCESS;
    }


    //마지막으로 회원 생성
    @PostMapping("/signup")
    public ApiResponse<String> createUser(@RequestBody @Valid CreateUserRequestDto request) {
        authService.createUser(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/signin")
    public ApiResponse<String> signinUser(@RequestBody SigninUserRequestDto request) {
        String response = authService.signinUser(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/check")
    ApiResponse<String> checkToken() {
        return ApiResponse.SUCCESS;
    }
}
