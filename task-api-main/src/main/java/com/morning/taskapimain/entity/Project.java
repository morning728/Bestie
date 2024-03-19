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
@Table("project")
public class Project {
    @Id
    @Column(name="id")
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_project",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName="id"))
    private Set<User> connectedUsers;

    public static Mono<Project> fromMap(Map<String, Object> map){
        return Mono.just(Project
                        .builder()
                        .id(Long.valueOf(map.get("id").toString()))
                        .name((String) map.get("name"))
                        .createdAt((LocalDateTime) map.get("created_at"))
                        .updatedAt((LocalDateTime) map.get("updated_at"))
                        .status((String) map.get("status"))
                        .description((String) map.get("description"))
                .build());
    }
}
