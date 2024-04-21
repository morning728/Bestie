package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FieldRepository extends R2dbcRepository<Field, Long> {
    Flux<Field> findByProjectId(Long id);
    Flux<Field> findByProjectIdOrderById(Long id);
}
