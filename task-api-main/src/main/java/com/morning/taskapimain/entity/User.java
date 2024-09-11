package com.morning.taskapimain.entity;


import com.morning.taskapimain.entity.dto.ProfileDTO;
import jakarta.persistence.*;
import lombok.*;
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
@Table("users")
public class User {
    @Id
    @Column(name="id")
    private Long id;
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

    public static Mono<User> monoFromMap(Map<String, Object> map){
        return Mono.just(User
                .builder()
                .id(Long.valueOf(map.get("id").toString()))
                .username((String) map.get("username"))
                .createdAt((LocalDateTime) map.get("created_at"))
                .updatedAt((LocalDateTime) map.get("updated_at"))
                .status((String) map.get("status"))
                .firstName((String) map.get("first_name"))
                .lastName((String) map.get("last_name"))
                .build());
    }
    public static User fromMap(Map<String, Object> map){
        return User
                .builder()
                .id(Long.valueOf(map.get("id").toString()))
                .username((String) map.get("username"))
                .createdAt((LocalDateTime) map.get("created_at"))
                .updatedAt((LocalDateTime) map.get("updated_at"))
                .status((String) map.get("status"))
                .firstName((String) map.get("first_name"))
                .lastName((String) map.get("last_name"))
                .build();
    }
    public static User defaultIfEmpty(){
        return User
                .builder()
                .status("EMPTY")
                .build();
    }

    public boolean isEmpty(){
        return (status != null && status.equals("EMPTY"));
    }

    public void updateByProfileDTO(ProfileDTO dto){
        this.setUpdatedAt(LocalDateTime.now());
        this.setStatus(dto.getStatus() != null ? dto.getStatus() : status);
        this.setFirstName(dto.getFirstName() != null ? dto.getFirstName() : firstName);
        this.setLastName(dto.getLastName() != null ? dto.getLastName() : lastName);
    }
}
