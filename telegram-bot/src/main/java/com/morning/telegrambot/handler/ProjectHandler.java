package com.morning.telegrambot.handler;

import com.morning.telegrambot.dto.project.ProjectDTO;
import com.morning.telegrambot.service.AuthService;
import com.morning.telegrambot.service.ProjectService;
import com.morning.telegrambot.service.TaskService;
import com.morning.telegrambot.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ProjectHandler {

    private final ProjectService projectService;
    private final TokenCacheService tokenCacheService;


    public String handleProjectCommand(Long chatId, String command) {
        String token = tokenCacheService.getToken(chatId);

        if (token == null) {
            return "❌ Вы не авторизованы. Используйте /login.";
        }

        List<ProjectDTO> projects = projectService.getMyProjects(token);
        return projects.isEmpty()
                ? "У вас пока нет проектов."
                : projects.stream()
                .map(project -> "📂 " + project.getTitle() + " (ID: " + project.getId() + ")")
                .collect(Collectors.joining("\n"));
    }
}




