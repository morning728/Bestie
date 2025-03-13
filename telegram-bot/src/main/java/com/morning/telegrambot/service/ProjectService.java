package com.morning.telegrambot.service;


import com.morning.telegrambot.client.TaskTrackerClient;
import com.morning.telegrambot.dto.project.ProjectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final TaskTrackerClient taskTrackerClient;

    public List<ProjectDTO> getMyProjects(String token) {
        return taskTrackerClient.getMyProjects("Bearer " + token);
    }

    public String getAllProjectsFormatted(String token) {
        List<ProjectDTO> projects = getMyProjects(token);
        return projects.isEmpty() ? "📌 У вас нет проектов." :
                projects.stream()
                        .map(project -> "📂 " + project.getTitle() + " (ID: " + project.getId() + ")")
                        .collect(Collectors.joining("\n"));
    }
}

