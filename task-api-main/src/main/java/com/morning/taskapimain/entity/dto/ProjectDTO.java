package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<User> connectedUsers;
}
