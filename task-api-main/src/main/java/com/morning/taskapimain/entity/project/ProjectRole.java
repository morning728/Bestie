package com.morning.taskapimain.entity.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    @Column(name = "permissions")
    private String permissions = String.valueOf(new EnumMap<>(Permission.class));

    public JsonObject permissionsToJsonObject() {
        return new Gson().fromJson(permissions, JsonObject.class);
    }
}

