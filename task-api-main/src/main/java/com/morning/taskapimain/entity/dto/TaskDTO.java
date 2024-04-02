package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.Task;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskDTO extends Task{
    public Task toInsertTask(){
        return Task.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(this.getName())
                .description(this.getDescription())
                .projectId(this.getProjectId())
                .fieldId(this.getFieldId())
                .build();
    }
}
