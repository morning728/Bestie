package com.morning.taskapimain.entity.task;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("task_reminder")
public class TaskReminder {
    @Id
    private Long id;
    private Long taskId;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
}
