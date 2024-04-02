package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ProjectDTO extends Project {
//    @Builder
//    public ProjectDTO(Long id, String name, String description, String status,
//                      String visibility, LocalDateTime createdAt, LocalDateTime updatedAt,
//                      Set<User> connectedUsers) {
//        super(id, name, description, status, visibility, createdAt, updatedAt, connectedUsers);
//    }
    public Project toInsertProject(){
        return Project.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .name(this.getName())
                .description(this.getDescription())
                .visibility(this.getVisibility())
                .build();
    }
}
