package com.morning.taskapimain.entity.kafka.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskNotificationEvent {
    private String action; // Например: ASSIGNED_TO_TASK, TASK_UPDATED, TASK_DEADLINE_SOON и т. д.
    private String taskTitle;
    private String projectTitle;
    private String username;      // username пользователя, которого касается уведомление
}

