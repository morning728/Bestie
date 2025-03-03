package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.ProjectTag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectTagRepository extends R2dbcRepository<ProjectTag, Long> {

    // 🔹 Получение тегов проекта
    @Query("SELECT * FROM project_tag WHERE project_id = :projectId")
    Flux<ProjectTag> findTagsByProjectId(@Param("projectId") Long projectId);

    // 🔹 Удаление всех тегов проекта
    @Query("DELETE FROM project_tag WHERE project_id = :projectId")
    Mono<Void> deleteTagsByProjectId(@Param("projectId") Long projectId);

    // 🔹 Сохранение нового тега в проект
    @Query("INSERT INTO project_tag (project_id, name, color) VALUES (:projectId, :name, :color) RETURNING *")
    Mono<ProjectTag> saveTag(@Param("projectId") Long projectId, @Param("name") String name, @Param("color") String color);
}
