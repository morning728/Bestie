package com.morning.mailnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}")
    private String from;
    @Value("${application.link-to-verify}")
    private String pathToVerify;

    private final JavaMailSender mailSender;
    private final JwtEmailService jwtEmailService;

    private final String verifyEmailPostfix = "/verify-email?data=";
    private final String VERIFICATION_SUBJECT = "Verification from Bestie!";
    private final String VERIFICATION_BODY = "Please, click on link if you are %s: %s";

    public void sendVerificationMail(Map<String, String> data){
        String username = data.get("username");
        String email = data.get("email");

        Map<String, String> claims = new HashMap<>();
        claims.put("email", email);

        String token = jwtEmailService.buildEmailVerificationToken(claims, username);

        String linkToVerify = pathToVerify.concat(verifyEmailPostfix).concat(token);

        String mailBody = String.format(VERIFICATION_BODY, username, linkToVerify);
        send(email, VERIFICATION_SUBJECT, mailBody);
    }


    private void send(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);
    }
}
