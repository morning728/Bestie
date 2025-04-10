package com.morning.telegrambot.dto;

import lombok.Data;

@Data
public class TaskAssigneeDTO {
    private Long id;
    private Long taskId;
    private Long userId;
}
