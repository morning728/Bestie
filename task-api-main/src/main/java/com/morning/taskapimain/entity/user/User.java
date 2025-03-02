package com.morning.taskapimain.entity.user;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User {
    @Id
    @Column(name = "id")
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }

    public static Mono<User> monoFromMap(Map<String, Object> map) {
        return Mono.just(fromMap(map));
    }

    public static User fromMap(Map<String, Object> map) {
        return User.builder()
                .id(Long.valueOf(map.get("id").toString()))
                .username((String) map.get("username"))
                .createdAt((LocalDateTime) map.get("created_at"))
                .updatedAt((LocalDateTime) map.get("updated_at"))
                .status((String) map.get("status"))
                .firstName((String) map.get("first_name"))
                .lastName((String) map.get("last_name"))
                .build();
    }

    public static User defaultIfEmpty() {
        return User.builder().status("EMPTY").build();
    }

    public boolean isEmpty() {
        return "EMPTY".equals(status);
    }
}
