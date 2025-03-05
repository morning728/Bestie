package com.morning.taskapimain.entity.project;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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


    // Это поле в БД (JSONB)
    @Column(name = "permissions")
    private String permissions;

    // Это поле в Java-коде
    @Transient
    private Map<Permission, Boolean> permissionsJson;

    @PostLoad
    public void deserializePermissions() {
        if (permissions != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                permissionsJson = objectMapper.readValue(permissions, new TypeReference<Map<Permission, Boolean>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse permissions JSON", e);
            }
        }
    }

    @PrePersist
    @PreUpdate
    public void serializePermissions() {
        if (permissionsJson != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                permissions = objectMapper.writeValueAsString(permissionsJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize permissions JSON", e);
            }
        }
    }
}

