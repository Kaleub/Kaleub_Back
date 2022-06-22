package com.photory.service.auth;

import com.photory.common.exception.model.*;
import com.photory.common.exception.test.ConflictException;
import com.photory.common.exception.test.NotFoundException;
import com.photory.controller.auth.dto.request.*;
import com.photory.domain.user.UserRole;
import com.photory.controller.auth.dto.response.SigninUserResponse;
import com.photory.domain.user.User;
import com.photory.domain.user.repository.UserRepository;
import com.photory.common.util.JwtUtil;
import com.photory.common.util.RedisUtil;
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

    public void validateEmail(ValidateEmailRequestDto validateEmailRequestDto) {

        String email = validateEmailRequestDto.getEmail();

        boolean emailDuplicate = userRepository.existsByEmail(email);

        if (emailDuplicate) {
            throw new ConflictException(String.format("이미 사용중인 (%s) 이메일입니다.", email), CONFLICT_EMAIL_EXCEPTION);
        }
    }

    public void authEmail(AuthEmailRequestDto authEmailRequestDto) {

        String email = authEmailRequestDto.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("이미 가입된 유저의 이메일 (%s) 입니다.", email), CONFLICT_USER_EXCEPTION);
        }
        String authKey = "";
        //임의의 authKey 생성
        do {
            Random random = new Random();
            authKey = String.valueOf(random.nextInt(888888) + 111111);
        } while(redisUtil.existKey(authKey));

        //이메일 발송
        sendAuthEmail(email, authKey);
    }

    public void authEmailComplete(AuthEmailCompleteRequestDto authEmailCompleteRequestDto) {

        String email = redisUtil.getData(authEmailCompleteRequestDto.getAuthKey());

        try {
            if (!email.equals(authEmailCompleteRequestDto.getEmail())) {
                throw new IncorrectAuthKeyException();
            }
        } catch (NullPointerException e) {
            throw new IncorrectAuthKeyException();
        }

        redisUtil.setDataExpire(email, "1", 60 * 60 * 24L);
    }

    public void createUser(CreateUserRequestDto createUserRequestDto) {
        String email = createUserRequestDto.getEmail();
        String password = createUserRequestDto.getPassword();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("이미 가입된 유저의 이메일 (%s) 입니다.", email), CONFLICT_USER_EXCEPTION);
        }

//       if (redisUtil.getData(email) != null && redisUtil.getData(email).compareTo("1") == 0) {
        User user = User.of(email, passwordEncoder.encode(password), UserRole.ROLE_USER);

        userRepository.save(user);
//        } else {
//            throw new UnAuthenticatedEmailException();
//        }
    }

    public SigninUserResponse signinUser(SigninUserRequestDto signinUserRequestDto) {

        String email = signinUserRequestDto.getEmail();
        String password = signinUserRequestDto.getPassword();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                String token = createToken(user.get());
                SigninUserResponse signinUserResponse = SigninUserResponse.of(token);

                return signinUserResponse;
            } else {
                throw new InvalidPasswordException();
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
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text,true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new MessageFailedException();
        }

        redisUtil.setDataExpire(authKey, email, 60 * 3L);
    }

    private String createToken(User user) {
        String token = jwtUtil.generateToken(user);
        redisUtil.setDataExpire(token, user.getEmail(), JwtUtil.TOKEN_VALIDATION_SECOND);

        return token;
    }
}
