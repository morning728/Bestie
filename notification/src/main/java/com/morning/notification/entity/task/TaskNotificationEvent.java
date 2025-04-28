package com.morning.notification.entity.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNotificationEvent {
    private String action; // Например: ASSIGNED_TO_TASK, STATUS_CHANGE, CREATE_REMINDER, DELETE_REMINDER
    private Long taskId;
    private String taskTitle;
    private String projectTitle;
    private String reminderText;
    private String remindAt;
    private String username;      // Для одиночных событий
    private String usernames;     // Для событий типа напоминания (через запятую)
}

