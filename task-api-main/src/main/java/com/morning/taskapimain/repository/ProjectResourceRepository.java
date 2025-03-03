package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.ProjectResource;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectResourceRepository extends R2dbcRepository<ProjectResource, Long> {

    // 🔹 Получение ресурсов проекта
    @Query("SELECT * FROM project_resource WHERE project_id = :projectId")
    Flux<ProjectResource> findResourcesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Удаление всех ресурсов проекта
    @Query("DELETE FROM project_resource WHERE project_id = :projectId")
    Mono<Void> deleteResourcesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Сохранение нового ресурса в проект
    @Query("INSERT INTO project_resource (project_id, url, description) VALUES (:projectId, :url, :description) RETURNING *")
    Mono<ProjectResource> saveResource(@Param("projectId") Long projectId, @Param("url") String url, @Param("description") String description);
}
