package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);

    Flux<User> findUserByUsernameContainsIgnoreCase(String substring);


}
