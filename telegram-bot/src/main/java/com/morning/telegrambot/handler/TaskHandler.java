package com.morning.telegrambot.handler;

import com.morning.telegrambot.dto.TaskDTO;
import com.morning.telegrambot.service.AuthService;
import com.morning.telegrambot.service.TaskService;
import com.morning.telegrambot.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskHandler {

    private final TaskService taskService;
    private final TokenCacheService tokenCacheService;

    public String handleTaskCommand(Long chatId, String command) {
        String token = tokenCacheService.getToken(chatId);

        if (token == null) {
            return "❌ Вы не авторизованы. Используйте /login.";
        }

        List<TaskDTO> tasks = taskService.getAllTasks(token);
        return tasks.isEmpty()
                ? "У вас пока нет задач."
                : tasks.stream()
                .map(task -> "📝 " + task.getTitle() + " (ID: " + task.getId() + ")")
                .collect(Collectors.joining("\n"));
    }

}

