package com.morning.taskapimain.entity.project;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("project")
public class Project {
    @Id
    @Column(name = "id")
    private Long id;

    private String title;
    private String description;
    private String color;
    private String icon;
    private String priority;
    private String status;
    private LocalDate deadline;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
