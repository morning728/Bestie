package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.*;
import com.morning.taskapimain.entity.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepository extends R2dbcRepository<Project, Long> {

    Flux<Project> findByOwnerId(Long ownerId);
    Mono<Project> findByTitle(String title);

    // 🔹 Получение тегов проекта
    @Query("SELECT * FROM project_tag WHERE project_id = :projectId")
    Flux<ProjectTag> findTagsByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение статусов проекта
    @Query("SELECT * FROM project_statuses WHERE project_id = :projectId")
    Flux<ProjectStatus> findStatusesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение ресурсов проекта
    @Query("SELECT * FROM project_resource WHERE project_id = :projectId")
    Flux<ProjectResource> findResourcesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Удаление всех тегов проекта
    @Query("DELETE FROM project_tag WHERE project_id = :projectId")
    Mono<Void> deleteTagsByProjectId(@Param("projectId") Long projectId);

    // 🔹 Удаление всех статусов проекта
    @Query("DELETE FROM project_statuses WHERE project_id = :projectId")
    Mono<Void> deleteStatusesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Удаление всех ресурсов проекта
    @Query("DELETE FROM project_resource WHERE project_id = :projectId")
    Mono<Void> deleteResourcesByProjectId(@Param("projectId") Long projectId);

    // 🔹 Сохранение нового тега в проект
    @Query("INSERT INTO project_tag (project_id, name, color) VALUES (:projectId, :name, :color) RETURNING *")
    Mono<ProjectTag> saveTag(@Param("projectId") Long projectId, @Param("name") String name, @Param("color") String color);

    // 🔹 Сохранение нового статуса в проект
    @Query("INSERT INTO project_statuses (project_id, name, color) VALUES (:projectId, :name, :color) RETURNING *")
    Mono<ProjectStatus> saveStatus(@Param("projectId") Long projectId, @Param("name") String name, @Param("color") String color);

    // 🔹 Сохранение нового ресурса в проект
    @Query("INSERT INTO project_resource (project_id, url, description) VALUES (:projectId, :url, :description) RETURNING *")
    Mono<ProjectResource> saveResource(@Param("projectId") Long projectId, @Param("url") String url, @Param("description") String description);

    // 🔹 Добавление пользователя в проект с ролью
    @Query("INSERT INTO user_project (project_id, user_id, role_id) VALUES (:projectId, :userId, " +
            "(SELECT id FROM project_role WHERE project_id = :projectId AND name = :roleName))")
    Mono<Void> assignUserToProject(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("roleName") String roleName);

    // 🔹 Удаление пользователя из проекта
    @Query("DELETE FROM user_project WHERE project_id = :projectId AND user_id = :userId")
    Mono<Void> removeUserFromProject(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // 🔹 Получение всех пользователей проекта
    @Query("SELECT u.* FROM user_project up JOIN \"user\" u ON up.user_id = u.id WHERE up.project_id = :projectId")
    Flux<User> findUsersByProjectId(@Param("projectId") Long projectId);

    // 🔹 Получение роли пользователя в проекте
    @Query("SELECT r.name FROM user_project up JOIN project_role r ON up.role_id = r.id " +
            "WHERE up.project_id = :projectId AND up.user_id = :userId")
    Mono<String> getUserRoleInProject(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // 🔹 Обновление роли пользователя в проекте
    @Query("UPDATE user_project SET role_id = " +
            "(SELECT id FROM project_role WHERE project_id = :projectId AND name = :newRole) " +
            "WHERE project_id = :projectId AND user_id = :userId")
    Mono<Void> updateUserRole(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("newRole") String newRole);

    // 🔹 Получение информации о роли пользователя
    @Query("SELECT * FROM project_role WHERE project_id = :projectId AND name = :roleName")
    Mono<ProjectRole> findRoleByProjectIdAndName(Long projectId, String roleName);

    // 🔹 Проверка, есть ли у роли определенное разрешение (использует JSONB)
    @Query("SELECT (permissions->>:#{#permission.name()})::BOOLEAN " +
            "FROM project_role WHERE name = :roleName AND project_id = :projectId")
    Mono<Boolean> hasPermission(@Param("roleName") String roleName, @Param("projectId") Long projectId, @Param("permission") Permission permission);

    // 🔹 Создание ролей по умолчанию
    @Query("INSERT INTO project_role (project_id, name, permissions) VALUES " +
            "(:projectId, 'Owner', '{" +
            "\"CAN_CREATE_TASKS\": true, \"CAN_EDIT_TASKS\": true, \"CAN_DELETE_TASKS\": true, " +
            "\"CAN_ARCHIVE_TASKS\": true, \"CAN_RESTORE_TASKS\": true, \"CAN_COMMENT_TASKS\": true, " +
            "\"CAN_ADD_REMINDERS\": true, \"CAN_EDIT_PROJECT\": true, \"CAN_MANAGE_MEMBERS\": true, " +
            "\"CAN_MANAGE_ROLES\": true }'::JSONB), " +
            "(:projectId, 'Member', '{" +
            "\"CAN_CREATE_TASKS\": true, \"CAN_EDIT_TASKS\": true, \"CAN_DELETE_TASKS\": false, " +
            "\"CAN_ARCHIVE_TASKS\": false, \"CAN_RESTORE_TASKS\": false, \"CAN_COMMENT_TASKS\": true, " +
            "\"CAN_ADD_REMINDERS\": true, \"CAN_EDIT_PROJECT\": false, \"CAN_MANAGE_MEMBERS\": false, " +
            "\"CAN_MANAGE_ROLES\": false }'::JSONB)")
    Mono<Void> createDefaultRoles(@Param("projectId") Long projectId);
}
