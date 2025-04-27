package com.morning.notification.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDTO {
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
    @Column(name= "task_reminder")
    private Boolean taskDeadlineReminder;
}
