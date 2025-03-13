package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);

    @Query("""
            SELECT u.id, u.username, u.first_name, u.last_name
            FROM user_project up
            JOIN app_user u ON up.user_id = u.id
            WHERE up.project_id = :projectId AND username = :username""")
    Mono<User> findByUsernameAndProjectId(@Param("username") String username, @Param("projectId") Long projectId);
}
