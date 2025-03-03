package com.morning.taskapimain.repository.task;

import com.morning.taskapimain.entity.project.ProjectTag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskTagRepository extends R2dbcRepository<ProjectTag, Long> {

    // 🔹 Получение тегов, назначенных задаче
    @Query("SELECT pt.* FROM task_tag tt JOIN project_tag pt ON tt.tag_id = pt.id WHERE tt.task_id = :taskId")
    Flux<ProjectTag> findTagsByTaskId(@Param("taskId") Long taskId);

    // 🔹 Добавление тега к задаче
    @Query("INSERT INTO task_tag (task_id, tag_id) VALUES (:taskId, :tagId) ON CONFLICT DO NOTHING")
    Mono<Void> addTagToTask(@Param("taskId") Long taskId, @Param("tagId") Long tagId);

    // 🔹 Удаление тега из задачи
    @Query("DELETE FROM task_tag WHERE task_id = :taskId AND tag_id = :tagId")
    Mono<Void> removeTagFromTask(@Param("taskId") Long taskId, @Param("tagId") Long tagId);
}
