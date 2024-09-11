package com.morning.openrequesthandler.service;

import com.morning.openrequesthandler.exception.BadRequestException;
import com.morning.openrequesthandler.repository.ProjectRepository;
import com.morning.openrequesthandler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.sql.*;
import java.util.Calendar;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final JwtEmailService jwtEmailService;
    private final SQLRequestExecutor sqlRequestExecutor;
    @Value("${application.security.database.task-api-main-db.url}")
    private String mainDatabasePath;
    @Value("${application.security.database.task-api-main-db.username}")
    private String mainDatabaseUsername;
    @Value("${application.security.database.task-api-main-db.password}")
    private String mainDatabasePassword;

    private static final String ADD_USER_TO_PROJECT_QUERY = """
        INSERT INTO public.user_project (project_id, user_id, role) VALUES (?, ?, 'GUEST')
        """;

    public void addUserToProjectByAcceptationToken(String acceptationToken) throws BadRequestException {
        Long projectId = jwtEmailService.extractProjectId(acceptationToken);
        String username = jwtEmailService.extractUsername(acceptationToken);

        try {
            sqlRequestExecutor.executeSQLRequestInMainDB(ADD_USER_TO_PROJECT_QUERY, projectId, userRepository.findByUsername(username).get().getId());
        } catch (Exception e){
            throw new BadRequestException("Something went wrong...");
        }
    }




}
