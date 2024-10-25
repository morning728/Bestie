package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String visibility;

    public Project toInsertProject() {
        return Project.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(this.getName())
                .description(this.getDescription())
                .visibility(this.getVisibility())
                .build();
    }

    // Метод для преобразования из Project в ProjectDTO
    public static ProjectDTO fromProject(Project project) {
        return new ProjectDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getVisibility()
        );
    }

    // Метод для преобразования из ProjectDTO в Project
    public Project toProject() {
        return Project.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .status(this.getStatus())
                .visibility(this.getVisibility())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

