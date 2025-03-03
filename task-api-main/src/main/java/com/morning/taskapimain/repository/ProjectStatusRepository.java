package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.ProjectStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectStatusRepository extends R2dbcRepository<ProjectStatus, Long> {

    // 🔹 Получение статусов проекта
    @Query("SELECT * FROM project_status WHERE project_id = :projectId")
    Flux<ProjectStatus> findStatusesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Удаление всех статусов проекта
    @Query("DELETE FROM project_status WHERE project_id = :projectId")
    Mono<Void> deleteStatusesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Сохранение нового статуса в проект
    @Query("INSERT INTO project_status (project_id, name, color) VALUES (:projectId, :name, :color) RETURNING *")
    Mono<ProjectStatus> saveStatus(@Param("projectId") Long projectId, @Param("name") String name, @Param("color") String color);
}
