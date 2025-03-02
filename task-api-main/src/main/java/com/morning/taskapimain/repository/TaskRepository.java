package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskComment;
import com.morning.taskapimain.entity.task.TaskReminder;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository extends R2dbcRepository<Task, Long> {

    // 🔹 Получение всех активных задач проекта
    @Query("SELECT * FROM tasks WHERE project_id = :projectId AND is_archived = FALSE")
    Flux<Task> findByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение всех архивных задач проекта
    @Query("SELECT * FROM tasks WHERE project_id = :projectId AND is_archived = TRUE")
    Flux<Task> findArchivedByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение задачи по ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Mono<Task> findById(@Param("taskId") Long taskId);

    // 🔹 Удаление задачи
    @Query("DELETE FROM tasks WHERE id = :taskId")
    Mono<Void> deleteTask(@Param("taskId") Long taskId);

    // 🔹 Архивирование задачи
    @Query("UPDATE tasks SET is_archived = TRUE, archived_at = CURRENT_TIMESTAMP WHERE id = :taskId")
    Mono<Void> archiveTask(@Param("taskId") Long taskId);

    // 🔹 Восстановление задачи из архива
    @Query("UPDATE tasks SET is_archived = FALSE, archived_at = NULL WHERE id = :taskId")
    Mono<Void> restoreTask(@Param("taskId") Long taskId);

    // 🔹 Обновление статуса задачи
    @Query("UPDATE tasks SET status_id = :statusId, updated_at = CURRENT_TIMESTAMP WHERE id = :taskId")
    Mono<Void> updateTaskStatus(@Param("taskId") Long taskId, @Param("statusId") Long statusId);

    // 🔹 Добавление напоминания к задаче
    @Query("INSERT INTO task_reminders (task_id, reminder_date, reminder_time) VALUES (:taskId, :reminderDate, :reminderTime) RETURNING *")
    Mono<TaskReminder> addReminder(@Param("taskId") Long taskId, @Param("reminderDate") String reminderDate, @Param("reminderTime") String reminderTime);

    // 🔹 Удаление всех напоминаний задачи
    @Query("DELETE FROM task_reminders WHERE task_id = :taskId")
    Mono<Void> deleteReminders(@Param("taskId") Long taskId);

    // 🔹 Получение комментариев к задаче
    @Query("SELECT * FROM task_comments WHERE task_id = :taskId ORDER BY created_at DESC")
    Flux<TaskComment> findCommentsByTaskId(@Param("taskId") Long taskId);

    // 🔹 Добавление комментария к задаче
    @Query("INSERT INTO task_comments (task_id, user_id, comment, created_at) VALUES (:taskId, :userId, :comment, CURRENT_TIMESTAMP) RETURNING *")
    Mono<TaskComment> addComment(@Param("taskId") Long taskId, @Param("userId") Long userId, @Param("comment") String comment);

    // 🔹 Удаление комментария
    @Query("DELETE FROM task_comments WHERE id = :commentId")
    Mono<Void> deleteComment(@Param("commentId") Long commentId);

    // 🔹 Проверка прав доступа через JSONB в `project_role`
    @Query("SELECT (permissions->>:#{#permission.name()})::BOOLEAN " +
            "FROM project_role WHERE name = :roleName AND project_id = :projectId")
    Mono<Boolean> hasPermission(@Param("roleName") String roleName, @Param("projectId") Long projectId, @Param("permission") Permission permission);
}
