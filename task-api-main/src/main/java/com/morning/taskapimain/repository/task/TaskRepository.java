package com.morning.taskapimain.repository.task;

import com.morning.taskapimain.entity.task.Task;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository extends R2dbcRepository<Task, Long> {

    // 🔹 Получение всех активных задач проекта
    @Query("SELECT * FROM task WHERE project_id = :projectId AND is_archived = FALSE")
    Flux<Task> findByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение всех архивных задач проекта
    @Query("SELECT * FROM task WHERE project_id = :projectId AND is_archived = TRUE")
    Flux<Task> findArchivedByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение задачи по ID
    @Query("SELECT * FROM task WHERE id = :taskId")
    Mono<Task> findById(@Param("taskId") Long taskId);

    // 🔹 Удаление задачи
    @Query("DELETE FROM task WHERE id = :taskId")
    Mono<Void> deleteTask(@Param("taskId") Long taskId);

    // 🔹 Архивирование задачи
    @Query("UPDATE task SET is_archived = TRUE, archived_at = CURRENT_TIMESTAMP WHERE id = :taskId")
    Mono<Void> archiveTask(@Param("taskId") Long taskId);

    // 🔹 Восстановление задачи из архива
    @Query("UPDATE task SET is_archived = FALSE, archived_at = NULL WHERE id = :taskId")
    Mono<Void> restoreTask(@Param("taskId") Long taskId);

    // 🔹 Обновление статуса задачи
    @Query("UPDATE task SET status_id = :statusId, updated_at = CURRENT_TIMESTAMP WHERE id = :taskId")
    Mono<Void> updateTaskStatus(@Param("taskId") Long taskId, @Param("statusId") Long statusId);
}
