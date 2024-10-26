package com.morning.taskapimain.unit;

import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Mock
    private DatabaseClient client;

    @Mock
    private DatabaseClient.GenericExecuteSpec executeSpec;

    @Mock
    private  org.springframework.r2dbc.core.FetchSpec<java.util.Map<String, Object>> genericExecuteSpec;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isParticipantOfProjectOrError_UserIsParticipant_ReturnsTrue() {
        // Arrange
        String token = "token";
        Long projectId = 1L;
        String username = "testUser";

        // Mock JWT service to return username from token
        when(jwtService.extractUsername(token)).thenReturn(username);

        // Mock the database client to return a valid project
        Map<String, Object> projectMap = new HashMap<>(); // Используем Map для возврата из first()
        projectMap.put("id", projectId);
        projectMap.put("name", "Test Project");
        projectMap.put("description", "Project Description");
        projectMap.put("status", "ACTIVE");
        projectMap.put("created_at", null); // Замените на нужное значение
        projectMap.put("updated_at", null); // Замените на нужное значение
        projectMap.put("visibility", "PUBLIC");

        when(client.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), anyString())).thenReturn(executeSpec); // Для username
        when(executeSpec.bind(anyString(), anyLong())).thenReturn(executeSpec); // Для projectId
        when(executeSpec.fetch()).thenReturn(genericExecuteSpec);
        when((genericExecuteSpec).first()).thenReturn(Mono.just(projectMap)); // Возвращаем пустой Mono

        // Act & Assert
        StepVerifier.create(projectService.isParticipantOfProjectOrError(projectId, token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isParticipantOfProjectOrError_UserIsNotParticipant_ReturnsError() {
        // Arrange
        String token = "token";
        Long projectId = 1L;
        String username = "testUser";

        // Mock JWT service to return username from token
        when(jwtService.extractUsername(token)).thenReturn(username);

        // Mock the database client to return an empty result
        when(client.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), anyString())).thenReturn(executeSpec); // Для username
        when(executeSpec.bind(anyString(), anyLong())).thenReturn(executeSpec); // Для projectId
        when(executeSpec.fetch()).thenReturn(genericExecuteSpec);
        when((genericExecuteSpec).first()).thenReturn(Mono.empty()); // Возвращаем пустой Mono

        // Act & Assert
        StepVerifier.create(projectService.isParticipantOfProjectOrError(projectId, token))
                .expectError(AccessException.class)
                .verify();
    }

    @Test
    void isAdminOfProjectOrError_UserIsAdmin_ReturnsTrue() {
        // Arrange
        String token = "token";
        Long projectId = 1L;
        String username = "testUser";

        // Mock JWT service to return username from token
        when(jwtService.extractUsername(token)).thenReturn(username);

        // Mock the database client to return a valid project with admin role
        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("id", projectId);
        projectMap.put("name", "Test Project");
        projectMap.put("description", "Project Description");
        projectMap.put("status", "ACTIVE");
        projectMap.put("created_at", null);
        projectMap.put("updated_at", null);
        projectMap.put("visibility", "PUBLIC");
        projectMap.put("role", "ADMIN"); // Указываем, что у пользователя есть роль ADMIN

        when(client.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), anyLong())).thenReturn(executeSpec);
        when(executeSpec.fetch()).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.first()).thenReturn(Mono.just(projectMap)); // Возвращаем проект

        // Act & Assert
        StepVerifier.create(projectService.isAdminOfProjectOrError(projectId, token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isAdminOfProjectOrError_UserIsNotAdmin_ReturnsError() {
        // Arrange
        String token = "token";
        Long projectId = 1L;
        String username = "testUser";

        // Mock JWT service to return username from token
        when(jwtService.extractUsername(token)).thenReturn(username);

        // Mock the database client to return an empty result
        when(client.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), anyLong())).thenReturn(executeSpec);
        when(executeSpec.fetch()).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.first()).thenReturn(Mono.empty()); // Возвращаем пустой Mono

        // Act & Assert
        StepVerifier.create(projectService.isAdminOfProjectOrError(projectId, token))
                .expectError(AccessException.class) // Ожидаем, что выбрасывается AccessException
                .verify();
    }

}
