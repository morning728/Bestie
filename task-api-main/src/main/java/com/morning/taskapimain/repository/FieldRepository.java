package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.Field;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface FieldRepository extends R2dbcRepository<Field, Long> {
    Flux<Field> findByProjectId(Long id);

    Flux<Field> findByProjectIdOrderById(Long id);
}
