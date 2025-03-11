package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskComment;
import com.morning.taskapimain.entity.task.TaskReminder;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.TaskService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class TaskController {

    private final TaskService taskService;
    private final JwtService jwtService;

    /**
     * ✅ Создание новой задачи
     */
    @PostMapping
    public Mono<ResponseEntity<Task>> createTask(@RequestBody TaskDTO taskDTO,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return taskService.createTask(taskDTO, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Получение всех задач проекта
     */
    @GetMapping("/project/{projectId}")
    public Flux<Task> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }
    /**
     * ✅ Обновление задачи
     */
    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<TaskDTO>> getTask(@PathVariable Long taskId,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return taskService.getFullTaskInfoById(taskId)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление задачи
     */
    @PutMapping("/{taskId}")
    public Mono<ResponseEntity<Task>> updateTask(@PathVariable Long taskId,
                                                 @RequestBody TaskDTO updatedTask,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return taskService.updateTask(taskId, updatedTask, token)
                .map(ResponseEntity::ok);
    }


    /**
     * ✅ Архивирование задачи
     */
    @PutMapping("/{taskId}/archive")
    public Mono<ResponseEntity<Void>> archiveTask(@PathVariable Long taskId,
                                                  @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return taskService.archiveTask(taskId, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
    /**
     * ✅ Архивирование задачи
     */
    @PutMapping("/{taskId}/restore")
    public Mono<ResponseEntity<Void>> restoreTask(@PathVariable Long taskId,
                                                  @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return taskService.restoreTask(taskId, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * ✅ Удаление задачи
     */
    @DeleteMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable Long taskId,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return taskService.deleteTask(taskId, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * ✅ Добавление комментария к задаче
     */
    @PostMapping("/{taskId}/comments")
    public Mono<ResponseEntity<TaskComment>> addComment(@PathVariable Long taskId,
                                                        @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                        @RequestParam String comment) {
        return taskService.addComment(taskId, token, comment)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Добавление напоминания к задаче
     */
/*    @PostMapping("/{taskId}/reminders")
    public Mono<ResponseEntity<TaskReminder>> addReminder(@PathVariable Long taskId,
                                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                          @RequestParam String reminderDate,
                                                          @RequestParam String reminderTime) {
        return taskService.addReminder(taskId, token, reminderDate, reminderTime)
                .map(ResponseEntity::ok);
    }*/

    /**
     * ✅ Управление тегами задачи (добавление/удаление)
     */
    @PutMapping("/{taskId}/tags")
    public Mono<ResponseEntity<Void>> manageTaskTags(@PathVariable Long taskId,
                                                     @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                     @RequestBody List<Long> tagIds) {
        return taskService.manageTaskTags(taskId, token, tagIds)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
