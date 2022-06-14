package com.kale.config.security;

import com.kale.exception.LoginException;
import com.kale.model.User;
import com.kale.util.CookieUtil;
import com.kale.util.JwtUtil;
import com.kale.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie jwtToken = cookieUtil.getCookie(request, JwtUtil.ACCESS_TOKEN_NAME);
        Cookie refreshJwtToken = cookieUtil.getCookie(request, JwtUtil.REFRESH_TOKEN_NAME);

        String email = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshEmail = null;

        try {
            if (jwtToken != null) {
                jwt = jwtToken.getValue();
                email = jwtUtil.getEmail(jwt);
            }
            if (email != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            jwtToken = null;
        } catch (Exception e) {
            throw new LoginException();
        }

        try {
            if (jwtToken == null && refreshJwtToken != null) {
                refreshJwt = refreshJwtToken.getValue();
                refreshEmail = redisUtil.getData(refreshJwt);

                if (refreshEmail.equals(jwtUtil.getEmail(refreshJwt))) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(refreshEmail);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    User user = User.builder()
                            .email(refreshEmail)
                            .build();
                    String newToken = jwtUtil.generateToken(user);

                    Cookie newAccessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, newToken);
                    response.addCookie(newAccessToken);
                }
            }
        } catch (ExpiredJwtException e) {
            throw new LoginException();
        } catch (Exception e) {
            throw new LoginException();
        }

        filterChain.doFilter(request, response);
    }
}