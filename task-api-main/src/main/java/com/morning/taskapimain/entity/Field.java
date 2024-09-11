package com.morning.taskapimain.entity;

import jakarta.persistence.Column;
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

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("field")
public class Field {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
    private Long projectId;

    public static Mono<Field> fromMap(Map<String, Object> map) {
        return Mono.just(Field
                .builder()
                .id(Long.valueOf(map.get("id").toString()))
                .name((String) map.get("name"))
                .projectId(Long.valueOf(map.get("project_id").toString()))
                .build());
    }

    public static Field defaultIfEmpty() {
        return Field
                .builder()
                .id(-1L)
                .build();
    }

    public boolean isEmpty(){
        return this.id == -1L;
    }
}
