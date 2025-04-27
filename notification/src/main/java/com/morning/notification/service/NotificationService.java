package com.morning.notification.service;

import com.morning.notification.entity.project.DeleteEvent;
import com.morning.notification.entity.project.InviteEvent;
import com.morning.notification.entity.task.TaskNotificationEvent;
import com.morning.notification.entity.user.NotificationPreferences;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final TelegramService telegramService;
    @Value("${spring.mail.username}")
    private String from;

    private void sendEmail(NotificationPreferences preferences, String subject, String body) {
        if (!preferences.getEmail().isEmpty() && preferences.getEmailNotification() && preferences.getEmailVerified()) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(preferences.getEmail());
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            mailMessage.setFrom(from);

            mailSender.send(mailMessage);
        }
    }




    public void sendInviteToProject(InviteEvent event) {
        String subject = "You have been invited to project!";
        String body = String.format(
                "%s, You have been invited to project %s by %s.\nIf you want to join, click here: %s",
                event.getUsername(),
                event.getProjectTitle(),
                event.getInvitedBy(),
                event.getInviteLink()
        );

        NotificationPreferences preferences = userService.getNotificationPreferencesByUsername(event.getUsername());
        if (preferences.getInviteEnabled()) {
            sendEmail(preferences, subject, body);
            telegramService.sendMessage(preferences, body);
        }
    }

    //проверь
    public void sendDeleteNotification(DeleteEvent event) {
        String subject = "You have been deleted from project.";
        String body = String.format(
                "%s, you have been removed from project \"%s\" by %s.",
                event.getUsername(),
                event.getProjectTitle(),
                event.getDeletedBy()
        );
        NotificationPreferences preferences = userService.getNotificationPreferencesByUsername(event.getUsername());
        if (preferences.getInviteEnabled()) {
            sendEmail(preferences, subject, body);
            telegramService.sendMessage(preferences, body);
        }
    }

    public void sendTaskAssignmentNotification(TaskNotificationEvent event) {
        String subject = "You have been assigned to task.";
        String body = String.format(
                "%s, You have been assigned to task <%s> in project <%s>.",
                event.getUsername(),
                event.getTaskTitle(),
                event.getProjectTitle()
        );
        NotificationPreferences preferences = userService.getNotificationPreferencesByUsername(event.getUsername());
        if (preferences.getTaskAssignedEnabled()) {
            sendEmail(preferences, subject, body);
            telegramService.sendMessage(preferences, body);
        }
    }

    public void sendStatusChangeNotification(TaskNotificationEvent event) {
        String subject = "The task you're responsible for has changed its status.";
        String body = String.format(
                "%s, The task <%s> in project <%s> you're responsible for has changed its status.",
                event.getUsername(),
                event.getTaskTitle(),
                event.getProjectTitle()
        );
        NotificationPreferences preferences = userService.getNotificationPreferencesByUsername(event.getUsername());
        if (preferences.getTaskUpdatedEnabled()) {
            sendEmail(preferences, subject, body);
            telegramService.sendMessage(preferences, body);
        }
    }

    public void sendTaskReminder(List<String> users, String taskName, String projectName, String reminderText) {
        for (String username : users) {
            String subject = "Напоминание от Bestie!";
            String body = String.format(
                    "Напоминание! Задача \"%s\" в проекте \"%s\": %s",
                    taskName,
                    projectName,
                    reminderText
            );

            NotificationPreferences preferences = userService.getNotificationPreferencesByUsername(username);
            if (preferences.getTaskDeadlineReminder()) {
                sendEmail(preferences, subject, body);
                telegramService.sendMessage(preferences, body);
            }
        }
    }


}
