package com.morning.taskapimain.repository.task;

import com.morning.taskapimain.entity.task.TaskAssignee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskAssigneeRepository extends R2dbcRepository<TaskAssignee, Long> {
    Flux<TaskAssignee> findByTaskId(Long taskId);

    Mono<TaskAssignee> save(TaskAssignee entity);

    @Query("INSERT INTO task_assignee (task_id, user_id) VALUES (:taskId, :userId) RETURNING *")
    Mono<TaskAssignee> addAssigneeToTask(@Param("taskId") Long taskId, @Param("userId") Long userId);

    Mono<Void> deleteByTaskId(Long taskId);

    Mono<Void> deleteByTaskIdAndAndUserId(Long taskId, Long userId);
}

