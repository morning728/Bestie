package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.project.ProjectTag;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskAssignee;
import com.morning.taskapimain.entity.task.TaskReminder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Long statusId;
    private List<Long> tagIds;
    private List<ProjectTag> tags;
    private List<TaskAssignee> assignees; // Список ответственных
    private List<Long> assigneeIds; // Список ответственных id
    private String priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long projectId;
    private String reminderText;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isArchived;

    public Task toTask() {
        return Task.builder()
                .id(id)
                .title(title)
                .description(description)
                .statusId(statusId)
                .priority(priority)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .projectId(projectId)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isArchived(isArchived)
                .build();
    }

    public static TaskDTO fromTask(Task task, List<ProjectTag> tags, TaskReminder reminder, List<TaskAssignee> assignees) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .statusId(task.getStatusId())
                .tags(tags) // Передаем список тегов
                .assignees(assignees)
                .priority(task.getPriority())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .projectId(task.getProjectId())
                .createdBy(task.getCreatedBy())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .isArchived(task.getIsArchived())
                .reminderDate(reminder != null ? reminder.getReminderDate() : null) // Если напоминание есть, устанавливаем дату
                .reminderTime(reminder != null ? reminder.getReminderTime() : null) // Если напоминание есть, устанавливаем время
                .build();
    }

}

