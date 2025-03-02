package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskComment;
import com.morning.taskapimain.entity.task.TaskReminder;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.repository.TaskRepository;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * ✅ Проверка, имеет ли пользователь право на действие с задачей
     */
    private Mono<Void> validateRequesterHasPermission(Long projectId, String token, Permission permission) {
        String requesterUsername = jwtService.extractUsername(token);
        return userRepository.findByUsername(requesterUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> projectRepository.getUserRoleInProject(projectId, user.getId())
                        .flatMap(role -> projectRepository.hasPermission(role, projectId, permission)
                                .filter(Boolean::booleanValue)
                                .switchIfEmpty(Mono.error(new AccessException("You don't have permission for this action!")))))
                .then();
    }

    /**
     * ✅ Создание задачи
     */
    public Mono<Task> createTask(Task task, String token) {
        return validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_CREATE_TASKS)
                .then(taskRepository.save(task));
    }

    /**
     * ✅ Получение всех задач проекта
     */
    public Flux<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    /**
     * ✅ Обновление задачи
     */
    public Mono<Task> updateTask(Long taskId, Task updatedTask, String token) {
        return validateRequesterHasPermission(updatedTask.getProjectId(), token, Permission.CAN_EDIT_TASKS)
                .then(taskRepository.save(updatedTask));
    }

    /**
     * ✅ Архивирование задачи
     */
    public Mono<Void> archiveTask(Long taskId, String token) {
        return validateRequesterHasPermission(taskId, token, Permission.CAN_ARCHIVE_TASKS)
                .then(taskRepository.archiveTask(taskId));
    }

    /**
     * ✅ Удаление задачи
     */
    public Mono<Void> deleteTask(Long taskId, String token) {
        return validateRequesterHasPermission(taskId, token, Permission.CAN_DELETE_TASKS)
                .then(taskRepository.deleteTask(taskId));
    }

    /**
     * ✅ Добавление комментария к задаче
     */
    public Mono<TaskComment> addComment(Long taskId, Long userId, String comment) {
        return taskRepository.addComment(taskId, userId, comment);
    }

    /**
     * ✅ Добавление напоминания
     */
    public Mono<TaskReminder> addReminder(Long taskId, String reminderDate, String reminderTime) {
        return taskRepository.addReminder(taskId, reminderDate, reminderTime);
    }
}
