package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.project.ProjectTag;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskComment;
import com.morning.taskapimain.entity.task.TaskReminder;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.*;
import com.morning.taskapimain.repository.task.*;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectUserRepository projectUserRepository;
    private final TaskReminderRepository taskReminderRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskTagRepository taskTagRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * ✅ Проверка, имеет ли пользователь право на действие с задачей
     */
    private Mono<Void> validateRequesterHasPermission(Long projectId, String token, Permission permission) {
        String requesterUsername = jwtService.extractUsername(token);

        return userRepository.findByUsername(requesterUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> projectRoleRepository.findRoleByProjectIdAndUserId(projectId, user.getId())
                        .switchIfEmpty(Mono.error(new AccessException("User does not have a role in this project!")))
                        .flatMap(role -> {
                            role.deserializePermissions();
                            // Проверяем наличие разрешения
                            if (Boolean.TRUE.equals(role.getPermissionsJson().get(permission))) {
                                return Mono.empty(); // Разрешение есть, продолжаем
                            } else {
                                return Mono.error(new AccessException("You don't have permission for this action!"));
                            }
                        })
                );
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
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_ARCHIVE_TASKS))
                .then(taskRepository.archiveTask(taskId));
    }

    /**
     * ✅ Восстановление задачи
     */
    public Mono<Void> restoreTask(Long taskId, String token) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_RESTORE_TASKS))
                .then(taskRepository.restoreTask(taskId));
    }

    /**
     * ✅ Удаление задачи
     */
    public Mono<Void> deleteTask(Long taskId, String token) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_DELETE_TASKS))
                .then(taskRepository.deleteTask(taskId));
    }

    /**
     * ✅ Добавление комментария к задаче
     */
    public Mono<TaskComment> addComment(Long taskId, String token, String comment) {
        String username = jwtService.extractUsername(token);

        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_COMMENT_TASKS)
                        .then(userService.getUserByUsername(username)) // Получаем User по username
                        .flatMap(user -> taskCommentRepository.addComment(taskId, user.getId(), comment))
                );
    }


    /**
     * ✅ Добавление напоминания
     */
    public Mono<TaskReminder> addReminder(Long taskId, String token, String reminderDate, String reminderTime) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_MANAGE_REMINDERS))
                .then(taskReminderRepository.addReminder(taskId, reminderDate, reminderTime));
    }

    /**
     * ✅ Управление тегами задачи (добавление/удаление)
     */
    public Mono<Void> manageTaskTags(Long taskId, String token, List<Long> tagIds) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_MANAGE_TASK_TAGS)
                        .thenMany(taskTagRepository.findTagsByTaskId(taskId)
                                .collectList()
                                .flatMap(existingTags -> {
                                    List<Long> existingTagIds = existingTags.stream().map(ProjectTag::getId).toList();

                                    // Определяем теги, которые нужно удалить
                                    List<Long> tagsToRemove = existingTagIds.stream()
                                            .filter(id -> !tagIds.contains(id))
                                            .toList();

                                    // Определяем теги, которые нужно добавить
                                    List<Long> tagsToAdd = tagIds.stream()
                                            .filter(id -> !existingTagIds.contains(id))
                                            .toList();

                                    // Удаляем старые теги и добавляем новые
                                    return Flux.concat(
                                            Flux.fromIterable(tagsToRemove)
                                                    .flatMap(tagId -> taskTagRepository.removeTagFromTask(taskId, tagId)),
                                            Flux.fromIterable(tagsToAdd)
                                                    .flatMap(tagId -> taskTagRepository.addTagToTask(taskId, tagId))
                                    ).then(); // ⬅️ Преобразуем Flux в Mono<Void>
                                })).then())
                .then(); // ⬅️ Финальный then(), чтобы вернуть Mono<Void>
    }



    /**
     * ✅ Управление статусом задачи
     */
    public Mono<Void> manageTaskStatus(Long taskId, String token, Long statusId) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_MANAGE_TASK_STATUSES))
                .then(taskRepository.updateTaskStatus(taskId, statusId));
    }
}

