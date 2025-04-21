package com.morning.notification.service;

import com.morning.notification.entity.project.DeleteEvent;
import com.morning.notification.entity.project.InviteEvent;
import com.morning.notification.entity.task.TaskNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;
    private final TelegramService telegramService;

    private void sendEmail(String to, String subject, String body) {
        if (!"no_data".equals(to)) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
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
        sendEmail(event.getEmail(), subject, body);
        telegramService.sendMessage(event.getChatId(), body);
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
        sendEmail(event.getEmail(), subject, body);
        telegramService.sendMessage(event.getChatId(), body);
    }

    public void sendTaskAssignmentNotification(TaskNotificationEvent event) {
        String subject = "You have been assigned to task.";
        String body = String.format(
                "%s, You have been assigned to task <%s> in project <%s>.",
                event.getUsername(),
                event.getTaskTitle(),
                event.getProjectTitle()
        );
        sendEmail(event.getEmail(), subject, body);
        telegramService.sendMessage(event.getChatId(), body);
    }

    public void sendStatusChangeNotification(TaskNotificationEvent event) {
        String subject = "The task you're responsible for has changed its status.";
        String body = String.format(
                "%s, The task <%s> in project <%s> you're responsible for has changed its status.",
                event.getUsername(),
                event.getTaskTitle(),
                event.getProjectTitle()
        );
        sendEmail(event.getEmail(), subject, body);
        telegramService.sendMessage(event.getChatId(), body);
    }


}
