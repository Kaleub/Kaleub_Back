package com.kale.service;

import com.kale.constant.Role;
import com.kale.dto.request.auth.AuthEmailCompleteReqDto;
import com.kale.dto.request.auth.CreateUserReqDto;
import com.kale.exception.*;
import com.kale.model.User;
import com.kale.repository.UserRepository;
import com.kale.util.JwtUtil;
import com.kale.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;

    public void validateEmail(String email) {

        boolean emailDuplicate = userRepository.existsByEmail(email);

        if (emailDuplicate) {
            throw new ExistingEmailException();
        }
    }

    public void authEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new ExistingEmailException();
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

    public void authEmailComplete(AuthEmailCompleteReqDto authEmailCompleteReqDto) {

        String email = redisUtil.getData(authEmailCompleteReqDto.getAuthKey());

        try {
            if (!email.equals(authEmailCompleteReqDto.getEmail())) {
                throw new IncorrectAuthKeyException();
            }
        } catch (NullPointerException e) {
            throw new IncorrectAuthKeyException();
        }

        redisUtil.setDataExpire(email, "1", 60 * 60 * 24L);
    }

    public void createUser(CreateUserReqDto createUserReqDto) {
        String email = createUserReqDto.getEmail();
        String password = createUserReqDto.getPassword();

        if (userRepository.existsByEmail(email)) {
            throw new ExistingEmailException();
        }

        if (redisUtil.getData(email).compareTo("1") == 0) {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(user);
        } else {
            throw new UnAuthenticatedEmailException();
        }

    }

    public User signinUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user.get();
            } else {
                throw new InvalidPasswordException();
            }
        } else {
            throw new NotFoundEmailException();
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



    public String createToken(User user) {
        String token = jwtUtil.generateToken(user);
        redisUtil.setDataExpire(token, user.getEmail(), JwtUtil.TOKEN_VALIDATION_SECOND);

        return token;
    }
}
