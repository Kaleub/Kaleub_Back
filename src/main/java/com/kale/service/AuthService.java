package com.kale.service;

import com.kale.constant.Role;
import com.kale.model.User;
import com.kale.repository.UserRepository;
import com.kale.util.CookieUtil;
import com.kale.util.JwtUtil;
import com.kale.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public User signUpUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            User newUser = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ROLE_USER)
                    .build();
            return userRepository.save(newUser);
        }
    }

    public User loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user.get();
            }
        }

        return null;
    }

    public Map<String, Cookie> createCookie(User user) {
        String token = jwtUtil.generateToken(user);
        String refreshJwt = jwtUtil.generateRefreshToken(user);
        Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
        Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
        redisUtil.setDataExpire(refreshJwt, user.getEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        Map<String, Cookie> map = new HashMap<>();
        map.put(JwtUtil.ACCESS_TOKEN_NAME, accessToken);
        map.put(JwtUtil.REFRESH_TOKEN_NAME, refreshToken);

        return map;
    }
}
