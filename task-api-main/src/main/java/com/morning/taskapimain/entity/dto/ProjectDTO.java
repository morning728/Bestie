package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.project.Project;
import com.morning.taskapimain.entity.project.ProjectRole;
import com.morning.taskapimain.entity.project.ProjectStatus;
import com.morning.taskapimain.entity.project.ProjectTag;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private List<ProjectTag> tags;
    private List<ProjectRole> roles;
    private List<ProjectStatus> statuses;
    private List<UserWithRoleDTO> members;

    public ProjectDTO(Project project){
        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.color = project.getColor();
        this.icon = project.getIcon();
        this.priority = project.getPriority();
        this.status = project.getStatus();
        this.deadline = project.getDeadline();
        this.ownerId = project.getOwnerId();
    }
}
