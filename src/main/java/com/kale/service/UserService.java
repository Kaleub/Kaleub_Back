package com.kale.service;

import com.kale.constant.Role;
import com.kale.dto.request.SignUpReqDto;
import com.kale.model.User;
import com.kale.repository.UserRepository;
import com.kale.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;


    @Transactional
    public User createUser(SignUpReqDto signUpReqDto){
        String email = signUpReqDto.getEmail();
        String password = signUpReqDto.getPassword();

        User user= User.builder()
                .email(email)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        return user;
    }


    @Transactional(readOnly = true)
    public String checkEmailDuplication(String email){

        boolean emailDuplicate = userRepository.existsByEmail(email);

        if (emailDuplicate){
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        return "가능한 이메일입니다.";
    }


    public void authEmail(String email) {

        //임의의 authKey 생성
        Random random= new Random();
        String authKey = String.valueOf(random.nextInt(888888)+11111);

        //이메일 발송
        sendAuthEmail(email,authKey);

    }

    private void sendAuthEmail(String email,String authKey) {

        String subject="제목";
        String text ="회원가입을 위한 인증번호는"+ authKey +"입니다.<br/>";

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text,true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        redisUtil.setDataExpire(authKey,email,60*3L);

    }
}
