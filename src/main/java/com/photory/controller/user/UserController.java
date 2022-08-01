package com.photory.controller.user;

import com.photory.common.dto.ApiResponse;
import com.photory.config.resolver.UserEmail;
import com.photory.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @DeleteMapping
    public ApiResponse<String> deleteUser(@UserEmail String userEmail) {
        userService.deleteUser(userEmail);
        return ApiResponse.SUCCESS;
    }
}
