package com.morning.taskapimain.entity.project;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("project_resource")
public class ProjectResource {
    @Id
    private Long id;
    private Long projectId;
    private String url;
    private String description;
}
