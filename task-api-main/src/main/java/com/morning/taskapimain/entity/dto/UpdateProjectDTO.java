package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.project.ProjectResource;
import com.morning.taskapimain.entity.project.ProjectStatus;
import com.morning.taskapimain.entity.project.ProjectTag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectDTO {
    private String title;
    private String description;
    private String color;
    private String icon;
    private String priority;
    private String status;
    private LocalDateTime deadline;

    private List<ProjectTag> tags;
    private List<ProjectStatus> statuses;
    private List<ProjectResource> resources;
}
