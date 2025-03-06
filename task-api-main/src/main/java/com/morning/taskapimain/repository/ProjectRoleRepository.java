package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.project.ProjectRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ProjectRoleRepository extends R2dbcRepository<ProjectRole, Long> {

    @Query("SELECT * FROM project_role WHERE project_id = :projectId")
    Flux<ProjectRole> getRolesByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT * FROM project_role WHERE id = :id")
    Mono<ProjectRole> findRoleById(@Param("id") Long id);

    // 🔹 Получение информации о роли пользователя
    @Query("SELECT pr.*\n" +
            "FROM project_role pr\n" +
            "JOIN user_project up ON pr.id = up.role_id\n" +
            "WHERE up.project_id = :projectId AND up.user_id = :userId;")
    Mono<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // 🔹 Создание ролей по умолчанию
    @Query("INSERT INTO project_role (project_id, name, permissions) VALUES " +
            "(:projectId, 'Owner', '{" +
            "\"CAN_CREATE_TASKS\": true, \"CAN_EDIT_TASKS\": true, \"CAN_DELETE_TASKS\": true,\"CAN_MANAGE_TASK_STATUSES\": true, \"CAN_MANAGE_TASK_TAGS\": true, " +
            "\"CAN_ARCHIVE_TASKS\": true, \"CAN_RESTORE_TASKS\": true, \"CAN_COMMENT_TASKS\": true, " +
            "\"CAN_MANAGE_REMINDERS\": true, \"CAN_EDIT_PROJECT\": true, \"CAN_MANAGE_MEMBERS\": true, " +
            "\"CAN_MANAGE_ROLES\": true }') RETURNING *")
    Mono<ProjectRole> createDefaultRoles(@Param("projectId") Long projectId);

    @Query("INSERT INTO project_role (project_id, name, permissions) VALUES (:projectId, :roleName, :permissions) RETURNING *")
    Mono<ProjectRole> addRole(@Param("projectId") Long projectId,
                              @Param("roleName") String roleName,
                              @Param("permissions") String permissions);

    @Query("UPDATE project_role SET name = :newRoleName, permissions = :permissions WHERE project_id = :projectId AND id = :projectRoleId RETURNING *")
    Mono<ProjectRole> updateRole(@Param("projectId") Long projectId,
                                 @Param("projectRoleId") Long projectRoleId,
                                 @Param("newRoleName") String newRoleName,
                                 @Param("permissions") String permissionsJson);

    @Query("SELECT COUNT(*) FROM project_role WHERE project_id = :projectId")
    Mono<Long> countRolesByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(*) FROM user_project WHERE role_id = :roleId")
    Mono<Long> countUsersWithRole(@Param("roleId") Long roleId);

    @Query("DELETE FROM project_role WHERE id = :roleId")
    Mono<Void> deleteRole(@Param("roleId") Long roleId);}


