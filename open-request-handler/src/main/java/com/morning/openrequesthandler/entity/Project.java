package com.morning.openrequesthandler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table("project")
public class Project {
    @Id
    @Column(name="id")
    private Integer id;
    private String name;
    private String description;
    private String status;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
