package com.morning.notification.service;

import com.morning.notification.entity.project.DeleteEvent;
import com.morning.notification.entity.project.InviteEvent;
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
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);
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
        if (!"no_data".equals(event.getEmail())) {
            sendEmail(event.getEmail(), subject, body);
        }

        if (!"no_data".equals(event.getChatId()) || !"no_data".equals(event.getTelegramId())) {
            telegramService.sendMessage(event.getChatId(), body);
        }
    }

//проверь
    public void sendDeleteNotification(DeleteEvent event) {
        String subject = "You have been deleted from project!";
        String body = String.format(
                "%s, you have been removed from project \"%s\" by %s.",
                event.getUsername(),
                event.getProjectTitle(),
                event.getDeletedBy()
        );
        if (!"no_data".equals(event.getEmail())) {
            sendEmail(event.getEmail(), subject, body);
        }

        if (!"no_data".equals(event.getChatId()) || !"no_data".equals(event.getTelegramId())) {
            telegramService.sendMessage(event.getChatId(), body);
        }
    }



 /*   public void sendDeleteNotification(Map<String, String> data){
        String username = data.get("username");
        String email = data.get("email");
        String from = data.get("from");
        String projectName = data.get("projectName");

        String mailBody = String.format(DELETE_FROM_PROJECT_BODY, username, projectName, from);
        send(email, DELETE_FROM_PROJECT_SUBJECT, mailBody);
    }

    public void sendTaskDeadlineReminder(List<String> emails,String taskName, String deadline){
        String mailBody = String.format("Deadline for task <<%s>> is burning! it expires on %s!", taskName, deadline);
        for(String email : emails){
            send(email, "Deadline is burning!:)", mailBody);
        }
    }*/

}
