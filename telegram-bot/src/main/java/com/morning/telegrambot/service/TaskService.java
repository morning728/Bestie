package com.morning.telegrambot.service;

import com.morning.telegrambot.client.TaskTrackerClient;
import com.morning.telegrambot.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskTrackerClient taskTrackerClient;

    public List<TaskDTO> getAllTasks(String token) {
        return taskTrackerClient.getAllTasks("Bearer " + token);
    }

    public String getAllTasksFormatted(String token) {
        List<TaskDTO> tasks = getAllTasks(token);
        return tasks.isEmpty() ? "📌 У вас нет задач." :
                tasks.stream()
                        .map(task -> "📝 " + task.getTitle() + " (ID: " + task.getId() + ")")
                        .collect(Collectors.joining("\n"));
    }
}

