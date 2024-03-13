package com.morning.taskapimain.repository;

import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepository  extends R2dbcRepository<Project, Long> {



}
