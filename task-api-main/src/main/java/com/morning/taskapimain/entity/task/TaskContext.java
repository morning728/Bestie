package com.morning.taskapimain.entity.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskContext {
    private Long taskId;
    private Long projectId;
    private String taskTitle;
    private String projectTitle;
    private List<String> assigneeUsernames; // уже готовые username ответственных
}
