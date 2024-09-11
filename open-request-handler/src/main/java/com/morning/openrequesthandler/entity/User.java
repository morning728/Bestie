package com.morning.openrequesthandler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
/*    Реактивщина ругается
    @ManyToMany(mappedBy = "connectedUsers", fetch = FetchType.EAGER)
    @JoinTable(name="user_project",
            joinColumns=  @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="project_id", referencedColumnName="id") )
    Set<Project> projects;*/

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
