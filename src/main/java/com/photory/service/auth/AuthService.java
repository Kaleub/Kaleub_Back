package com.photory.service.auth;

import com.photory.common.exception.model.ConflictException;
import com.photory.common.exception.model.InternalServerException;
import com.photory.common.exception.model.NotFoundException;
import com.photory.common.exception.model.ValidationException;
import com.photory.common.util.JwtUtil;
import com.photory.common.util.RedisUtil;
import com.photory.controller.auth.dto.request.AuthEmailCompleteRequestDto;
import com.photory.controller.auth.dto.request.AuthEmailRequestDto;
import com.photory.controller.auth.dto.request.SigninUserRequestDto;
import com.photory.controller.auth.dto.request.ValidateEmailRequestDto;
import com.photory.domain.user.User;
import com.photory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

import static com.photory.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;

    public void validateEmail(ValidateEmailRequestDto request) {

        String email = request.getEmail();

        boolean emailDuplicate = userRepository.existsByEmail(email);

        if (emailDuplicate) {
            throw new ConflictException(String.format("이미 사용중인 (%s) 이메일입니다.", email), CONFLICT_EMAIL_EXCEPTION);
        }
    }

    public void authEmail(AuthEmailRequestDto request) {

        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("이미 가입된 유저의 이메일 (%s) 입니다.", email), CONFLICT_USER_EXCEPTION);
        }
        String authKey = "";
        //임의의 authKey 생성
        do {
            Random random = new Random();
            authKey = String.valueOf(random.nextInt(888888) + 111111);
        } while (redisUtil.existKey(authKey));

        //이메일 발송
        sendAuthEmail(email, authKey);
    }

    public void authEmailComplete(AuthEmailCompleteRequestDto request) {

        String email = redisUtil.getData(request.getAuthKey());

        try {
            if (!email.equals(request.getEmail())) {
                throw new ValidationException("잘못된 이메일 인증번호입니다.", VALIDATION_EMAIL_AUTH_KEY_EXCEPTION);
            }
        } catch (NullPointerException e) {
            throw new ValidationException("잘못된 이메일 인증번호입니다.", VALIDATION_EMAIL_AUTH_KEY_EXCEPTION);
        }

        redisUtil.setDataExpire(email, "1", 60 * 60 * 24L);
    }

    public String signinUser(SigninUserRequestDto request) {

        String email = request.getEmail();
        String password = request.getPassword();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                String token = createToken(user.get());

                return token;
            } else {
                throw new ValidationException("잘못된 비밀번호입니다.", VALIDATION_WRONG_PASSWORD_EXCEPTION);
            }
        } else {
            throw new NotFoundException(String.format("가입되지 않은 이메일 (%s) 입니다", email), NOT_FOUND_EMAIL_EXCEPTION);
        }
    }

    private void sendAuthEmail(String email, String authKey) {

        String subject = "제목";
        String text = "회원가입을 위한 인증번호는 " + authKey + " 입니다.<br/>";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new InternalServerException(String.format("(%s) 이메일에 대한 인증 메일을 전송하는 중 에러가 발생했습니다.", email));
        }

        redisUtil.setDataExpire(authKey, email, 60 * 3L);
    }

    private String createToken(User user) {
        String token = jwtUtil.generateToken(user);
        redisUtil.setDataExpire(token, user.getEmail(), JwtUtil.TOKEN_VALIDATION_SECOND);

        return token;
    }
}
