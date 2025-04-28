package com.morning.notification.service;


import com.morning.notification.entity.user.Contacts;
import com.morning.notification.entity.user.NotificationPreferences;
import com.morning.notification.entity.user.NotificationPreferencesDTO;
import com.morning.notification.entity.user.TelegramDataEvent;
import com.morning.notification.repository.NotificationPreferencesRepository;
import com.morning.notification.service.security.JwtEmailService;
import com.morning.notification.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final JwtService jwtService;
    private final JwtEmailService jwtEmailService;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Value("${application.link-to-verify}")
    private String verificationLinkStartsWith;

    public Contacts getContactsByUsername(String username) {
        return notificationPreferencesRepository.findContactsByUsername(username)
                .orElse(null);
    }

    public NotificationPreferences getNotificationPreferencesByUsername(String username) {
        return notificationPreferencesRepository.findByUsername(username)
                .orElse(null);
    }

    public NotificationPreferences updatePreferences(String token, NotificationPreferencesDTO dto) {
        String username = jwtService.extractUsername(token);
        NotificationPreferences preferences = notificationPreferencesRepository.findByUsername(username).orElseThrow();
        preferences.setUpdatedAt(LocalDateTime.now());
        preferences.setEmailNotification(dto.getEmailNotification());
        preferences.setTelegramNotification(dto.getTelegramNotification());
        preferences.setInviteEnabled(dto.getInviteEnabled());
        preferences.setTaskAssignedEnabled(dto.getTaskAssignedEnabled());
        preferences.setTaskUpdatedEnabled(dto.getTaskUpdatedEnabled());
        preferences.setTaskReminder(dto.getTaskReminder());
        return notificationPreferencesRepository.save(preferences);
    }

    public void verifyEmail(String token, String emailToken) {
        NotificationPreferences user = notificationPreferencesRepository.findByUsername(jwtService.extractUsername(token)).orElseThrow();

        if (jwtEmailService.isTokenValid(emailToken, jwtService.extractUsername(token)) &&
                jwtEmailService.extractEmail(emailToken).equals(user.getEmail())) {
            user.setEmailVerified(true);
            notificationPreferencesRepository.save(user);
        }
    }

    public void setEmail(String token, String newEmail) {
        String username = jwtService.extractUsername(token);
        NotificationPreferences user = notificationPreferencesRepository.findByUsername(username).orElseThrow();
        if(user.getEmail() == newEmail)
            return;
        user.setEmail(newEmail);
        user.setEmailVerified(false);
        notificationPreferencesRepository.save(user);
        String verificationLink = verificationLinkStartsWith.concat(jwtEmailService.buildEmailVerificationToken(
                Map.of("email", newEmail),
                username
        ));
        sendEmailVerification(username, newEmail, verificationLink);
    }

    public void sendEmailVerification(String username, String newEmail, String verificationLink) {
        String subject = "Verify Email, Please!";
        String body = String.format(
                "This email was listed as the main one on the" +
                        " Bestie website by %s. If it was you, please " +
                        "follow the link to confirm your email, the link is valid " +
                        "for 10 hours. The link: %s",
                username,
                verificationLink
        );

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);
    }

    public void updateTelegramData(TelegramDataEvent event) {
        NotificationPreferences user = notificationPreferencesRepository.findByUsername(event.getUsername()).orElseThrow();
        user.setTelegramId(event.getTelegramId());
        user.setChatId(event.getChatId());
        notificationPreferencesRepository.save(user);
    }

    public NotificationPreferences addUser(String username) {
        Optional<NotificationPreferences> user = notificationPreferencesRepository.findByUsername(username);
        if(user.isEmpty())
            return notificationPreferencesRepository.save(NotificationPreferences.builder()
                            .username(username)
                            .build());
        return user.get();
    }

    public NotificationPreferences getNotificationPreferencesByToken(String token) {
        return getNotificationPreferencesByUsername(jwtService.extractUsername(token));
    }
}
