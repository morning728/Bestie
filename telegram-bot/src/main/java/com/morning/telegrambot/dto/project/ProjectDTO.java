package com.morning.telegrambot.dto.project;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
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
    private List<ProjectTagDTO> tags;
    private List<ProjectRoleDTO> roles;
    private List<ProjectStatusDTO> statuses;
    private List<UserWithRoleDTO> members;
}
