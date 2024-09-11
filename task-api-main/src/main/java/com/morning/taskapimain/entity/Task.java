package com.morning.taskapimain.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.exception.NotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuperBuilder(toBuilder = true)
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
    private LocalDateTime finishDate;
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

    public <T extends Task> Task toUpdate(T from){
        this.setId(from.getId());
        this.setFinishDate(from.getFinishDate() == null ? getFinishDate() : from.getFinishDate());
        this.setUpdatedAt(LocalDateTime.now());
        this.setCreatedAt(from.getCreatedAt() == null ? getCreatedAt() : from.getCreatedAt());
        this.setName(from.getName() == null ? name : from.getName());
        this.setStatus(from.getStatus() == null ? status : from.getStatus());
        this.setDescription(from.getDescription() == null ? description : from.getDescription());
        this.setFieldId(from.getFieldId() == null ? this.getFieldId() : from.getFieldId());
        this.setProjectId(from.getProjectId() == null ? this.getProjectId() : from.getProjectId());
        return this;
    }

    public static Task defaultIfEmpty() {
        return Task
                .builder()
                .status("EMPTY")
                .build();
    }

    public boolean isEmpty(){
        if(status != null)
            return status.equals("EMPTY");
        return false;
    }

    public Mono<Task> returnExceptionIfEmpty(){
        {
            if(this.isEmpty()){
                return Mono.error(new NotFoundException("Task was not found!"));
            }
            return Mono.just(this);
        }
    }
}