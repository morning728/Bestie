package com.morning.taskapimain.entity.task;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("task")
public class Task {
    @Id
    @Column(name = "id")
    private Long id;

    private String title;
    private String description;
    private Long statusId;
    private String priority;
    @Column(name = "start_date")
    private LocalDate startDate; // Изменено на LocalDate (только дата)

    @Column(name = "end_date")
    private LocalDate endDate; // Изменено на LocalDate (только дата)

    @Column(name = "start_time")
    private LocalTime startTime; // Изменено на LocalTime (только время)

    @Column(name = "end_time")
    private LocalTime endTime; // Изменено на LocalTime (только время)
    private Boolean isArchived;
    private LocalDateTime archivedAt;
    private Long projectId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
