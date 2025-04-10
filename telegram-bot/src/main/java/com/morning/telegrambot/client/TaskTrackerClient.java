package com.morning.telegrambot.client;


import com.morning.telegrambot.dto.TaskDTO;
import com.morning.telegrambot.dto.project.ProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "taskTrackerClient", url = "http://localhost:8765/api/v1")
public interface TaskTrackerClient {

    // 🔹 Методы для задач
    @GetMapping("/tasks")
    List<TaskDTO> getAllTasks(@RequestHeader("Authorization") String token);

    @PostMapping("/tasks")
    TaskDTO createTask(@RequestHeader("Authorization") String token, @RequestBody TaskDTO taskDTO);

    @PutMapping("/tasks/{taskId}")
    TaskDTO updateTask(@RequestHeader("Authorization") String token, @PathVariable Long taskId, @RequestBody TaskDTO taskDTO);

    @DeleteMapping("/tasks/{taskId}")
    void deleteTask(@RequestHeader("Authorization") String token, @PathVariable Long taskId);



    @GetMapping("/projects/my")
    List<ProjectDTO> getMyProjects(@RequestHeader("Authorization") String token);

    @GetMapping("/projects/{projectId}")
    ProjectDTO getProjectById(@RequestHeader("Authorization") String token, @PathVariable Long projectId);
}


