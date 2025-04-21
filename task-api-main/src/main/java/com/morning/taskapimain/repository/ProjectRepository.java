package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.Project;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepository extends R2dbcRepository<Project, Long> {
    @Query("SELECT p.title FROM project p WHERE p.id = :projectId")
    Mono<String> findProjectTitleById(@Param("projectId") Long projectId);

    Mono<Project> findById(Long aLong);

    Flux<Project> findByOwnerId(Long ownerId);

    Mono<Project> findByTitle(String title);

    // 🔹 Удаление всех ресурсов проекта
    @Query("SELECT p.id, p.title, p.description, p.color, p.icon, p.priority, p.status, p.deadline, p.owner_id" +
            " FROM project p JOIN user_project up ON p.id = up.project_id WHERE  up.user_id = :userId")
    Flux<Project> findAllByUserId(@Param("userId") Long userId);
}
