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
    @Value("${application.link-to-accept-invitation-to-project}")
    private String pathToAcceptAnInvitationToProject;

    private final JavaMailSender mailSender;
    private final JwtEmailService jwtEmailService;

    private final String verifyEmailPostfix = "/verify?data=";
    private final String acceptInvitationPostfix = "/accept-invitation?data=";
    private final String VERIFICATION_SUBJECT = "Verification from Bestie!";
    private final String VERIFICATION_BODY = "Please, click on link if you are %s: %s";
    private final String ACCEPTION_TO_PROJECT_SUBJECT = "You have been invited to project!";
    private final String ACCEPTION_TO_PROJECT_BODY = "%s, You have been invited to project by %s, if you want to join, click: %s";
    private final String DELETE_FROM_PROJECT_BODY = "%s, You have been deleted from project <<%s>> by %s!";
    private final String DELETE_FROM_PROJECT_SUBJECT = "You have been deleted from project!";

    public void sendVerificationMail(Map<String, String> data){
        String username = data.get("username");
        String email = data.get("email");

        Map<String, String> claims = new HashMap<>();
        claims.put("email", email);

        String token = jwtEmailService.buildEmailToken(claims, username);

        String linkToVerify = pathToVerify.concat(verifyEmailPostfix).concat(token);

        String mailBody = String.format(VERIFICATION_BODY, username, linkToVerify);
        send(email, VERIFICATION_SUBJECT, mailBody);
    }

    public void sendInviteToProject(Map<String, String> data){
        String username = data.get("username");
        String email = data.get("email");
        String from = data.get("from");

        Map<String, String> claims = new HashMap<>();
        claims.put("projectId", data.get("projectId"));

        String token = jwtEmailService.buildEmailToken(claims, username);

        String linkToAccept = pathToAcceptAnInvitationToProject.concat(acceptInvitationPostfix).concat(token);

        String mailBody = String.format(ACCEPTION_TO_PROJECT_BODY, username, from, linkToAccept);
        send(email, ACCEPTION_TO_PROJECT_SUBJECT, mailBody);
    }

    public void sendDeleteNotification(Map<String, String> data){
        String username = data.get("username");
        String email = data.get("email");
        String from = data.get("from");
        String projectName = data.get("projectName");

        String mailBody = String.format(DELETE_FROM_PROJECT_BODY, username, projectName, from);
        send(email, DELETE_FROM_PROJECT_SUBJECT, mailBody);
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
