package com.morning.taskapimain.entity.project;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("project_statuses")
public class ProjectStatus {
    @Id
    private Long id;
    private Long projectId;
    private String name;
    private String color;
}
