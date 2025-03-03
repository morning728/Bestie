package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.project.ProjectRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface ProjectRoleRepository extends R2dbcRepository<ProjectRole, Long> {

    // 🔹 Получение информации о роли пользователя
    @Query("SELECT * FROM project_role WHERE project_id = :projectId AND name = :roleName")
    Mono<ProjectRole> findRoleByProjectIdAndName(@Param("projectId") Long projectId, @Param("roleName") String roleName);

    // 🔹 Проверка, есть ли у роли определенное разрешение (использует JSONB)
    @Query("SELECT (permissions->>:#{#permission.name()})::BOOLEAN " +
            "FROM project_role WHERE name = :roleName AND project_id = :projectId")
    Mono<Boolean> hasPermission(@Param("roleName") String roleName, @Param("projectId") Long projectId, @Param("permission") Permission permission);

    // 🔹 Создание ролей по умолчанию
    @Query("INSERT INTO project_role (project_id, name, permissions) VALUES " +
            "(:projectId, 'Owner', '{" +
            "\"CAN_CREATE_TASKS\": true, \"CAN_EDIT_TASKS\": true, \"CAN_DELETE_TASKS\": true,\"CAN_MANAGE_TASK_STATUSES\": true, \"CAN_MANAGE_TASK_TAGS\": true, " +
            "\"CAN_ARCHIVE_TASKS\": true, \"CAN_RESTORE_TASKS\": true, \"CAN_COMMENT_TASKS\": true, " +
            "\"CAN_MANAGE_REMINDERS\": true, \"CAN_EDIT_PROJECT\": true, \"CAN_MANAGE_MEMBERS\": true, " +
            "\"CAN_MANAGE_ROLES\": true }'::JSONB)")
    Mono<Void> createDefaultRoles(@Param("projectId") Long projectId);
}
