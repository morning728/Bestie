package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.Task;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskDTO extends Task{
    public Task toInsertTask(){
        return Task.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .finishDate(LocalDateTime.now().plusHours(24))
                .name(this.getName())
                .description(this.getDescription())
                .projectId(this.getProjectId())
                .fieldId(this.getFieldId())
                .build();
    }
}
