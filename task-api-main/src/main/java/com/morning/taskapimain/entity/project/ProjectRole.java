package com.morning.taskapimain.entity.project;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.EnumMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("project_role")
public class ProjectRole {
    @Id
    private Long id;

    private Long projectId;
    private String name;

    // 🔹 Используем Map<Permission, Boolean> вместо множества полей
    private Map<Permission, Boolean> permissions = new EnumMap<>(Permission.class);

    /**
     * ✅ Проверка, имеет ли роль разрешение
     */
    public boolean hasPermission(Permission permission) {
        return permissions.getOrDefault(permission, false);
    }
}

