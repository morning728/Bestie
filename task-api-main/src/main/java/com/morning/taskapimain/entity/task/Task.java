package com.morning.taskapimain.entity.task;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("tasks")
public class Task {
    @Id
    @Column(name = "id")
    private Long id;

    private String title;
    private String description;
    private Long statusId;
    private Long tagId;
    private String priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String startTime;
    private String endTime;
    private Boolean isArchived;
    private LocalDateTime archivedAt;
    private Long projectId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
