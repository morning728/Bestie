package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.Task;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime finishDate;
    private Long projectId;
    private Long fieldId;

    @JsonProperty("list_of_responsible")
    private List<String> listOfResponsible = new ArrayList<>();

    // Метод для преобразования из TaskDTO в Task
    public Task toTask() {
        return Task.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .status(this.status)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .updatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now())
                .finishDate(this.finishDate != null ? this.finishDate : LocalDateTime.now().plusHours(48))
                .projectId(this.projectId)
                .fieldId(this.fieldId)
                .build();
    }

    // Метод для преобразования Task в TaskDTO с добавлением списка ответственных пользователей
    public static TaskDTO toTaskDTO(Task task, List<String> responsibleUsers) {
        return TaskDTO.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .finishDate(task.getFinishDate())
                .projectId(task.getProjectId())
                .fieldId(task.getFieldId())
                .listOfResponsible(responsibleUsers)
                .build();
    }

    // Метод для создания Task из DTO для первоначальной вставки
    public Task toInsertTask() {
        return Task.builder()
                .name(this.name)
                .description(this.description)
                .status(this.status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .finishDate(LocalDateTime.now().plusHours(48))
                .projectId(this.projectId)
                .fieldId(this.fieldId)
                .build();
    }
}

