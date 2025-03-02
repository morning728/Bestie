package com.morning.taskapimain.entity.task;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("task_comments")
public class TaskComment {
    @Id
    private Long id;
    private Long taskId;
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;
}
