package com.morning.taskapimain.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.GigaChatConfig;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GigaChatService {

    private final GigaChatConfig config;
    private final TaskService taskService;

    @Qualifier("webClientGigaChat")
    private final WebClient webClient;

    public Mono<String> getAccessToken() {
        return webClient.post()
                .uri("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .header("RqUID", UUID.randomUUID().toString())
                .header("Authorization", "Basic " + config.getAuthorizationKey())
                .bodyValue("scope=" + config.getScope())
                .retrieve()
                .bodyToMono(Map.class)
                .map(json -> (String) json.get("access_token"));
    }

    public Mono<Void> splitAndCreateSubtasks(TaskDTO originalTask, int count, String token) {

        return generateSubtasks(originalTask, count)
                .map(this::extractJsonArray)
                .flatMapMany(Flux::fromIterable)
                .flatMap(subtaskJson -> {
                    TaskDTO subtask = TaskDTO.builder().build();
                    subtask.setTitle((String) subtaskJson.get("title"));
                    subtask.setDescription((String) subtaskJson.get("description"));

                    String periodStr = (String) subtaskJson.get("period");
                    String[] parts = periodStr.split(" - ");
                    if (parts.length == 2) {
                        LocalDateTime start = LocalDateTime.parse(parts[0]);
                        LocalDateTime end = LocalDateTime.parse(parts[1]);

                        subtask.setStartDate(start.toLocalDate());
                        subtask.setStartTime(start.toLocalTime());
                        subtask.setEndDate(end.toLocalDate());
                        subtask.setEndTime(end.toLocalTime());
                    }


                    subtask.setStatusId(originalTask.getStatusId());
                    subtask.setTagIds(originalTask.getTagIds());
                    subtask.setAssigneeIds(originalTask.getAssigneeIds());
                    subtask.setPriority(originalTask.getPriority());
                    subtask.setProjectId(originalTask.getProjectId());

                    return taskService.createTask(subtask, token);
                })
                .then(); // вернуть Mono<Void>
    }


    public Mono<String> generateSubtasks(TaskDTO originalTask, int count) {
        String prompt = generatePromptFromTask(originalTask, count);

        return getAccessToken().flatMap(token ->
                webClient.post()
                        .uri("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
                        .header("Authorization", "Bearer " + token)
                        .header("Accept", "application/json")
                        .bodyValue(Map.of(
                                "model", "GigaChat",
                                "temperature", 0.5, // уменьшаем температурный коэффициент
                                "top_p", 0.8,       // чуть снижаем top-p
                                "max_tokens", 2048,
                                "repetition_penalty", 1.1,
                                "n", 1,
                                "messages", List.of(
                                        Map.of("role", "system", "content", "Ты помощник по управлению задачами."),
                                        Map.of("role", "user", "content", prompt)
                                )
                        ))
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }


    private String generatePromptFromTask(TaskDTO task, int count) {
        return String.format("""
                        # Требуется разложить основную задачу на подзадачи

                        Основная задача:
                            
                        Заголовок задачи: "%s"
                        Описание задачи: "%s"
                        Период времени: "%sT%s - %sT%s"

                        ## Необходимо разделить её на ровно %d подзадач. Количество подзадач должно быть строго фиксировано!

                        ### Примеры правильного формата:

                        * Правильно:
                          ```json
                          [
                            {"title": "Подзадача 1", "description": "Подробное описание...", "period": "начало-подзадачи - конец-подзадачи"},
                            ...
                          ]
                          ```
                          
                        * Неправильно:
                          ```json
                          ["Только названия подзадач"]
                          ```

                        Результат должен соответствовать указанному формату и включать строго %d подзадач.
                        """,
                task.getTitle(),
                task.getDescription(),
                task.getStartDate(), task.getStartTime(),
                task.getEndDate(), task.getEndTime(),
                count, count // повторяем число, чтобы подчеркнуть необходимость точного числа подзадач
        );
    }


    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> extractJsonArray(String responseJson) {
        try {
            JsonNode root = new ObjectMapper().readTree(responseJson);

            // Путь: choices[0].message.content
            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // Теперь из content извлекаем JSON-массив в квадратных скобках
            Pattern pattern = Pattern.compile("\\[.*?]", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String jsonArray = matcher.group();
                return new ObjectMapper().readValue(jsonArray, List.class);
            } else {
                throw new RuntimeException("JSON-массив не найден в content");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга JSON-массива: " + e.getMessage(), e);
        }
    }


}
