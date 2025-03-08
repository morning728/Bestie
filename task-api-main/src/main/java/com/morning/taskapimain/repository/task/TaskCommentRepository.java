package com.morning.taskapimain.repository.task;

import com.morning.taskapimain.entity.task.TaskComment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskCommentRepository extends R2dbcRepository<TaskComment, Long> {
    // 🔹 Получение комментария по ID
    @Query("SELECT * FROM task_comment WHERE id = :commentId")
    Mono<TaskComment> findCommentById(@Param("commentId") Long commentId);

    Mono<TaskComment> findTaskCommentById(@Param("id") Long id);
    // 🔹 Получение комментариев к задаче
    @Query("SELECT * FROM task_comment WHERE task_id = :taskId ORDER BY created_at DESC")
    Flux<TaskComment> findCommentsByTaskId(@Param("taskId") Long taskId);

    // 🔹 Добавление комментария к задаче
    @Query("INSERT INTO task_comment (task_id, user_id, comment, created_at) VALUES (:taskId, :userId, :comment, CURRENT_TIMESTAMP) RETURNING *")
    Mono<TaskComment> addComment(@Param("taskId") Long taskId, @Param("userId") Long userId, @Param("comment") String comment);

    // 🔹 Удаление комментария
    @Query("DELETE FROM task_comment WHERE id = :commentId")
    Mono<Void> deleteComment(@Param("commentId") Long commentId);
}
