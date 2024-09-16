package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.Task;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class TaskDTO extends Task{
    @JsonProperty("list_of_responsible")
    private List<String> listOfResponsible = new ArrayList<>();



    public Task toInsertTask(){
        return Task.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .finishDate(LocalDateTime.now().plusHours(48))
                .name(this.getName())
                .description(this.getDescription())
                .projectId(this.getProjectId())
                .fieldId(this.getFieldId())
                .build();
    }

    // Метод для преобразования Task в TaskDTO с добавлением списка ответственных пользователей
    public static TaskDTO toTaskDTO(Task task, List<String> responsibleUsers) {
        return TaskDTO.builder()
                .id(task.getId())
                .fieldId(task.getFieldId())
                .description(task.getDescription())
                .name(task.getName())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .finishDate(task.getFinishDate())
                .projectId(task.getProjectId())
                .listOfResponsible(responsibleUsers)
                .build();
    }
}
