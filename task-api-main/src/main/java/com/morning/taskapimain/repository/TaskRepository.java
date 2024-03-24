package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TaskRepository extends R2dbcRepository<Task, Long> {

}

