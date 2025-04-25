package com.morning.notification.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    @Id
    private Long id;
    private String username;
    @Column(name= "chat_id")
    private Long chatId;
    @Column(name= "telegram_id")
    private String telegramId;
    private String email;
    @Column(name= "email_verified")
    private Boolean emailVerified;
    @Column(name= "email_notification")
    private Boolean emailNotification;
    @Column(name= "telegram_notification")
    private Boolean telegramNotification;
    @Column(name= "invite_enabled")
    private Boolean inviteEnabled;
    @Column(name= "task_assigned_enabled")
    private Boolean taskAssignedEnabled;
    @Column(name= "task_updated_enabled")
    private Boolean taskUpdatedEnabled;
    @Column(name= "task_deadline_reminder")
    private Boolean taskDeadlineReminder;
    @Column(name= "created_at")
    private LocalDateTime createdAt;
    @Column(name= "updated_at")
    private LocalDateTime updatedAt;

}
