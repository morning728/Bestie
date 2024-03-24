package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FieldRepository extends R2dbcRepository<Field, Long> {
}
