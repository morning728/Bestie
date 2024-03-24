package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.Task;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TaskInsertDTO {
    private String name;
    private String description;
    private Long projectId;
    private Long fieldId;

    public Task toTask(){
        return Task.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(name)
                .description(description)
                .projectId(projectId)
                .fieldId(fieldId)
                .build();
    }
}
