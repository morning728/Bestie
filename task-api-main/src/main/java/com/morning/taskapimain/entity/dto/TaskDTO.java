package com.morning.taskapimain.entity.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskDTO {
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
}
