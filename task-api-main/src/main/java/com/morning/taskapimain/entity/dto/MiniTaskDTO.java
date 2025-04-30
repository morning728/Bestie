package com.morning.taskapimain.entity.dto;

import com.morning.taskapimain.entity.task.Task;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MiniTaskDTO {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long projectId;

    public static MiniTaskDTO fromTask(Task task) {
        return MiniTaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .projectId(task.getProjectId())
                .build();
    }
}
