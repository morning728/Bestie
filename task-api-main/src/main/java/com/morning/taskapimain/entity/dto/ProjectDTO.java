package com.morning.taskapimain.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private String color;
    private String icon;
    private String priority;
    private String status;
    private LocalDate deadline;
    private Long ownerId;
}
