package com.morning.taskapimain.repository.task;

import com.morning.taskapimain.entity.project.ProjectStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskStatusRepository extends R2dbcRepository<ProjectStatus, Long> {

    // 🔹 Получение статусов, доступных для задач в проекте
    @Query("SELECT * FROM project_status WHERE project_id = :projectId")
    Flux<ProjectStatus> findStatusesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Обновление статуса задачи
    @Query("UPDATE task SET status_id = :statusId, updated_at = CURRENT_TIMESTAMP WHERE id = :taskId")
    Mono<Void> updateTaskStatus(@Param("taskId") Long taskId, @Param("statusId") Long statusId);
}
