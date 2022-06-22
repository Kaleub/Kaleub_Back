package com.photory.service;

import com.photory.constant.Role;
import com.photory.dto.request.auth.*;
import com.photory.dto.response.auth.SigninUserResDto;
import com.photory.exception.*;
import com.photory.domain.User;
import com.photory.repository.UserRepository;
import com.photory.util.JwtUtil;
import com.photory.util.RedisUtil;
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

    public void validateEmail(ValidateEmailReqDto validateEmailReqDto) {

        String email = validateEmailReqDto.getEmail();

        boolean emailDuplicate = userRepository.existsByEmail(email);

        if (emailDuplicate) {
            throw new ExistingEmailException();
        }
    }

    public void authEmail(AuthEmailReqDto authEmailReqDto) {

        String email = authEmailReqDto.getEmail();

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

//        if (redisUtil.getData(email) != null && redisUtil.getData(email).compareTo("1") == 0) {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ROLE_USER)
                    .build();

            userRepository.save(user);
//        } else {
//            throw new UnAuthenticatedEmailException();
//        }
    }

    public SigninUserResDto signinUser(SigninUserReqDto signinUserReqDto) {

        String email = signinUserReqDto.getEmail();
        String password = signinUserReqDto.getPassword();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                String token = createToken(user.get());
                SigninUserResDto signinUserResDto = SigninUserResDto.builder()
                        .token(token)
                        .build();

                return signinUserResDto;
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

    private String createToken(User user) {
        String token = jwtUtil.generateToken(user);
        redisUtil.setDataExpire(token, user.getEmail(), JwtUtil.TOKEN_VALIDATION_SECOND);

        return token;
    }
}
