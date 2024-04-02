package com.morning.taskapimain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("task")
public class Task {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private Long fieldId;

    public static Mono<Task> fromMap(Map<String, Object> map) {
        return Mono.just(Task
                .builder()
                .id(Long.valueOf(map.get("id").toString()))
                .name((String) map.get("name"))
                .createdAt((LocalDateTime) map.get("created_at"))
                .updatedAt((LocalDateTime) map.get("updated_at"))
                .status((String) map.get("status"))
                .description((String) map.get("description"))
                .projectId(Long.valueOf(map.get("project_id").toString()))
                .fieldId(Long.valueOf(map.get("field_id").toString()))
                .build());
    }

    public static Task defaultIfEmpty() {
        return Task
                .builder()
                .status("EMPTY")
                .build();
    }
}