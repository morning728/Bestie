package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectUserRepository extends R2dbcRepository<User, Long> {

    // 🔹 Добавление пользователя в проект с ролью
    @Query("INSERT INTO user_project (project_id, user_id, role_id) VALUES (:projectId, :userId, " +
            "(SELECT id FROM project_role WHERE project_id = :projectId AND name = :roleName))")
    Mono<Void> assignUserToProject(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("roleName") String roleName);

    // 🔹 Удаление пользователя из проекта
    @Query("DELETE FROM user_project WHERE project_id = :projectId AND user_id = :userId")
    Mono<Void> removeUserFromProject(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // 🔹 Получение всех пользователей проекта
    @Query("SELECT u.* FROM user_project up JOIN app_user u ON up.user_id = u.id WHERE up.project_id = :projectId")
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
}
