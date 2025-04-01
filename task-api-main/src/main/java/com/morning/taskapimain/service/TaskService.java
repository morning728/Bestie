package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.project.ProjectTag;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskAssignee;
import com.morning.taskapimain.entity.task.TaskComment;
import com.morning.taskapimain.entity.task.TaskReminder;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.*;
import com.morning.taskapimain.repository.task.*;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final TaskReminderRepository taskReminderRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final ProjectTagRepository projectTagRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final TaskTagRepository taskTagRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final ProjectUserRepository projectUserRepository;
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

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
     * ✅ Получение полной информации по задаче
     */
    public Mono<TaskDTO> getFullTaskInfoById(Long taskId) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> {
                    Mono<List<ProjectTag>> tagsMono = taskTagRepository.findTagsByTaskId(taskId).collectList();
                    Mono<TaskReminder> reminderMono = taskReminderRepository.findByTaskId(taskId)
                            .switchIfEmpty(Mono.just(new TaskReminder())); // ⬅️ Избегаем `null`
                    Mono<List<TaskAssignee>> assingeesMono = taskAssigneeRepository.findByTaskId(taskId).collectList();
                    return Mono.zip(tagsMono, reminderMono, assingeesMono)
                            .map(tuple -> TaskDTO.fromTask(task, tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }

    /**
     * ✅ Получение всех задач проекта
     */
    public Flux<TaskDTO> getFullInfoActiveTasksByProject(Long projectId) {
        return taskRepository.findActiveByProjectId(projectId)
                .flatMap(task -> getFullTaskInfoById(task.getId()));
    }
    /**
     * ✅ Получение всех задач проекта
     */
    public Flux<TaskDTO> getFullInfoArchivedTasksByProject(Long projectId) {
        return taskRepository.findArchivedByProjectId(projectId)
                .flatMap(task -> getFullTaskInfoById(task.getId()));
    }

    public Flux<TaskDTO> getFullInfoAllTasksByProject(Long projectId) {
        return taskRepository.findAllByProjectId(projectId)
                .flatMap(task -> getFullTaskInfoById(task.getId()));
    }

    public Flux<ProjectTag> getTagsByTaskId(Long taskId) {
        return (taskTagRepository.findTagsByTaskId(taskId));
    }

    public Mono<Task> getTaskById(Long taskId) {
        return (taskRepository.findById(taskId))
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")));
    }

    /**
     * ✅ Создание задачи (с тегами и статусом)
     */
    public Mono<Task> createTask(TaskDTO taskDTO, String token) {
        return validateRequesterHasPermission(taskDTO.getProjectId(), token, Permission.CAN_CREATE_TASKS)
                .then(userService.getUserId(jwtService.extractUsername(token)))
                .flatMap(userId -> {
                    taskDTO.setUpdatedAt(LocalDateTime.now());
                    taskDTO.setCreatedAt(LocalDateTime.now());
                    taskDTO.setCreatedBy(userId);
                    taskDTO.setTagIds(taskDTO.getTagIds() == null ? new ArrayList<>() : taskDTO.getTagIds());
                    taskDTO.setAssigneeIds(taskDTO.getAssigneeIds() == null ? new ArrayList<>() : taskDTO.getAssigneeIds());

                    Task task = taskDTO.toTask(); // Конвертация DTO в Task
                    task.setIsArchived(false);
                    return taskRepository.save(task)
                            .flatMap(savedTask ->
                                    manageTaskTags(savedTask.getId(), token, taskDTO.getTagIds())
                                            .then(taskDTO.getAssigneeIds().isEmpty() ?
                                                    manageTaskAssignees(savedTask.getId(), token, List.of(savedTask.getCreatedBy())) :
                                                    manageTaskAssignees(savedTask.getId(), token, taskDTO.getAssigneeIds()))
                                            .then(manageReminder(savedTask.getId(), token, taskDTO.getReminderDate(), taskDTO.getReminderTime()))
                                            .thenReturn(savedTask));
                });
    }


    /**
     * ✅ Обновление задачи (с тегами, статусами и напоминаниями)
     */
    public Mono<Task> updateTask(Long taskId, TaskDTO updatedTask, String token) {
        return validateRequesterHasPermission(updatedTask.getProjectId(), token, Permission.CAN_EDIT_TASKS)
                .then(taskRepository.findById(taskId))
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setPriority(updatedTask.getPriority());
                    existingTask.setStatusId(updatedTask.getStatusId());
                    existingTask.setStartDate(updatedTask.getStartDate());
                    existingTask.setEndDate(updatedTask.getEndDate());
                    existingTask.setStartTime(updatedTask.getStartTime());
                    existingTask.setEndTime(updatedTask.getEndTime());
                    existingTask.setUpdatedAt(LocalDateTime.now());
                    updatedTask.setTagIds(updatedTask.getTagIds() == null ? new ArrayList<>() : updatedTask.getTagIds());
                    updatedTask.setAssigneeIds(updatedTask.getAssigneeIds() == null ? new ArrayList<>() : updatedTask.getAssigneeIds());

                    return taskRepository.save(existingTask)
                            .flatMap(savedTask -> manageTaskTags(savedTask.getId(), token, updatedTask.getTagIds())
                                    .then(manageTaskAssignees(savedTask.getId(), token, updatedTask.getAssigneeIds()))
                                    .then(manageReminder(taskId, token, updatedTask.getReminderDate(), updatedTask.getReminderTime()))
                                    .thenReturn(savedTask));
                });
    }

    /*    *//**
     * ✅ Обновление тегов задачи
     *//*
    private Mono<Void> updateTaskTags(Long taskId, List<Long> tagIds) {
        return taskTagRepository.deleteTagsByTaskId(taskId)
                .thenMany(Flux.fromIterable(tagIds)
                        .flatMap(tagId -> taskTagRepository.addTagToTask(taskId, tagId)))
                .then();
    }*/

    /**
     * ✅ Получение всех задач проекта
     */
    public Flux<Task> getTasksByProject(Long projectId) {
        return taskRepository.findAllByProjectId(projectId);
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
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_COMMENT_TASKS))
                .then(userService.getUserId(jwtService.extractUsername(token)))
                .flatMap(userId -> taskCommentRepository.addComment(taskId, userId, comment));
    }

    /**
     * ✅ Удаление комментария по ID
     */
    public Mono<Void> deleteComment(Long commentId) {
        return taskCommentRepository.findCommentById(commentId)
                .switchIfEmpty(Mono.error(new NotFoundException("Comment not found")))
                .flatMap(comment -> taskCommentRepository.deleteComment(commentId));
    }

    /**
     * ✅ Управление напоминанием задачи (создание/обновление/удаление)
     */
    public Mono<Void> manageReminder(Long taskId, String token, LocalDate reminderDate, LocalTime reminderTime) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_MANAGE_REMINDERS)
                        .then(
                                taskReminderRepository.findByTaskId(taskId)
                                        .switchIfEmpty(
                                                // Если напоминания не было — создаем
                                                reminderDate != null && reminderTime != null
                                                        ? taskReminderRepository.addReminder(taskId, reminderDate, reminderTime)
                                                        : Mono.empty()
                                        )
                                        .flatMap(existing -> {
                                            // Если даты совпадают — ничего не делаем
                                            if (Objects.equals(existing.getReminderDate(), reminderDate) &&
                                                    Objects.equals(existing.getReminderTime(), reminderTime)) {
                                                return Mono.empty();
                                            }

                                            // Если новые даты null → удаление
                                            if (reminderDate == null || reminderTime == null) {
                                                return taskReminderRepository.deleteReminderByTaskId(taskId);
                                            }
                                            System.out.println(existing.toString());
                                            // Иначе — обновление
                                            return taskReminderRepository.updateReminder(existing.getId(), reminderDate, reminderTime).then();
                                        })

                        )
                ).then();
    }


    /**
     * ✅ Управление тегами задачи (добавление/удаление)
     */
    public Mono<Void> manageTaskTags(Long taskId, String token, List<Long> tagIds) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_MANAGE_TASK_TAGS)
                        .thenMany((taskTagRepository.findTagsByTaskId(taskId)
                                .collectList()
                                .zipWith(projectTagRepository.findTagsByProjectId(task.getProjectId()).collectList()))
                                .flatMap(objects -> {
                                    List<Long> existingProjectTagIds = objects.getT2().stream().map(ProjectTag::getId).toList();
                                    List<Long> existingTaskTagIds = objects.getT1().stream().map(ProjectTag::getId).toList();

                                    // Определяем теги, которые нужно удалить
                                    List<Long> tagsToRemove = existingTaskTagIds.stream()
                                            .filter(id -> !tagIds.contains(id))
                                            .toList();

                                    // Определяем теги, которые нужно добавить
                                    List<Long> tagsToAdd = tagIds.stream()
                                            .filter(id -> !existingTaskTagIds.contains(id) && existingProjectTagIds.contains(id))
                                            .toList();

                                    // Удаляем старые теги и добавляем новые
                                    return Flux.concat(
                                            Flux.fromIterable(tagsToRemove)
                                                    .flatMap(tagId -> taskTagRepository.deleteTagFromTask(taskId, tagId)),
                                            Flux.fromIterable(tagsToAdd)
                                                    .flatMap(tagId -> taskTagRepository.addTagToTask(taskId, tagId))
                                    ).then(); // ⬅️ Преобразуем Flux в Mono<Void>
                                })).then())
                .then(); // ⬅️ Финальный then(), чтобы вернуть Mono<Void>
    }

    /**
     * ✅ Управление ответственными задачи (добавление/удаление)
     */
    public Mono<Void> manageTaskAssignees(Long taskId, String token, List<Long> userIds) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new NotFoundException("Task not found")))
                .flatMap(task -> validateRequesterHasPermission(task.getProjectId(), token, Permission.CAN_MANAGE_ASSIGNEES)
                        .thenMany((taskAssigneeRepository.findByTaskId(taskId)
                                .collectList()
                                .zipWith(projectUserRepository.findUsersByProjectId(task.getProjectId()).collectList()))
                                .flatMap(objects -> {

                                    List<Long> existingProjectUsersIds = objects.getT2().stream().map(User::getId).toList();
                                    List<Long> existingTaskAssigneesIds = objects.getT1().stream().map(TaskAssignee::getId).toList();

                                    // Определяем ответственных, которые нужно удалить
                                    List<Long> assigneesToRemove = existingTaskAssigneesIds.stream()
                                            .filter(id -> !userIds.contains(id))
                                            .toList();

                                    // Определяем ответственных, которые нужно добавить
                                    List<Long> assigneesToAdd = userIds.stream()
                                            .filter(id -> !existingTaskAssigneesIds.contains(id) && existingProjectUsersIds.contains(id))
                                            .toList();

                                    // Удаляем старые теги и добавляем новые
                                    return Flux.concat(
                                            Flux.fromIterable(assigneesToRemove)
                                                    .flatMap(userId -> taskAssigneeRepository.deleteByTaskIdAndAndUserId(taskId, userId)),
                                            Flux.fromIterable(assigneesToAdd)
                                                    .flatMap(userId -> taskAssigneeRepository.addAssigneeToTask(taskId, userId))
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

