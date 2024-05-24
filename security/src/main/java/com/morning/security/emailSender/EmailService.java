package com.morning.security.emailSender;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
public class EmailService {
    public static final String EMAIL = "matveygrom2017@yandex.ru";

    private final JavaMailSender javaMailSender;

    @Async
    public void sendNotification(String header, String text) throws MailException{
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(EMAIL);
        mail.setFrom(EMAIL);
        mail.setSubject(header);
        mail.setText(text);

        javaMailSender.send(mail);
    }


}









































