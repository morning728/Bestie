package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.Task;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long fieldId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task toInsertTask(){
        return Task.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(name)
                .description(description)
                .projectId(projectId)
                .fieldId(fieldId)
                .build();
    }
    public Task toUpdateTask(){
        return Task.builder()
                .id(id)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .name(name)
                .description(description)
                .projectId(projectId)
                .fieldId(fieldId)
                .status(status)
                .build();
    }
}
