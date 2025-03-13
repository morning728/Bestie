package com.morning.telegrambot.dto;

import com.morning.telegrambot.dto.ProjectTagDTO;
import com.morning.telegrambot.dto.TaskAssigneeDTO;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Long statusId;
    private List<Long> tagIds;
    private List<ProjectTagDTO> tags;
    private List<Long> assigneeIds;
    private List<TaskAssigneeDTO> assignees;
    private String priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long projectId;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private Long createdBy;
    private String createdAt;
    private String updatedAt;
}
