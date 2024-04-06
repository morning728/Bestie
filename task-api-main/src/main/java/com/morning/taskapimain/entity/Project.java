package com.morning.taskapimain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
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
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
/*    Реактивщина ругается

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties
    @JoinTable(
            name = "user_project",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName="id"))
    private Set<User> connectedUsers;*/

    public static Mono<Project> fromMap(Map<String, Object> map){
        return Mono.just(Project
                        .builder()
                        .id(Long.valueOf(map.get("id").toString()))
                        .name((String) map.get("name"))
                        .createdAt((LocalDateTime) map.get("created_at"))
                        .updatedAt((LocalDateTime) map.get("updated_at"))
                        .status((String) map.get("status"))
                        .description((String) map.get("description"))
                        .visibility((String) map.get("visibility"))
                .build());
    }


    public <T extends Project> void update(T from){
        this.setUpdatedAt(LocalDateTime.now());
        this.setName(from.getName() == null ? name : from.getName());
        this.setStatus(from.getStatus() == null ? status : from.getStatus());
        this.setDescription(from.getDescription() == null ? description : from.getDescription());
        this.setVisibility(from.getVisibility() == null ? visibility : from.getVisibility());
    }

    public static Project defaultIfEmpty(){
        return Project
                .builder()
                .status("EMPTY")
                .build();
    }
    public boolean isEmpty(){
        return status == "EMPTY";
    }
}
