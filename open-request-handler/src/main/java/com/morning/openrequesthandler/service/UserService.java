package com.morning.openrequesthandler.service;

import com.morning.openrequesthandler.exception.BadRequestException;
import com.morning.openrequesthandler.repository.ProjectRepository;
import com.morning.openrequesthandler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
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

    private static final String VERIFY_EMAIL_QUERY = """
            UPDATE public.auth_user SET verified = 'true' WHERE username = ? ;
            """;

    public void verifyEmail(String acceptationToken) throws BadRequestException {
        String username = jwtEmailService.extractUsername(acceptationToken);

        try {
            sqlRequestExecutor.executeSQLRequestInSecurityDB(VERIFY_EMAIL_QUERY, username);
        } catch (Exception e){
            throw new BadRequestException("Something went wrong...");
        }
    }

}
