package com.morning.taskapimain.integration;

import com.morning.taskapimain.Init.PostgresDatabaseContainerInitializer;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(initializers = PostgresDatabaseContainerInitializer.class)
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})  // Отключаем Kafka конфигурацию
class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private JwtService jwtService; // Мокаем JwtService

    @Autowired
    private DatabaseClient client;

    public static String token = "Bearer token";

    // isParticipantOfProjectOrError
    @Test
    void isParticipantOfProjectOrError_UserIsParticipant_ReturnsTrue() {
        // Arrange
        Long projectId = 1L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "testUser"
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.isParticipantOfProjectOrError(projectId, token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isParticipantOfProjectOrError_UserIsNotParticipant_ReturnsError() {
        // Arrange
        Long projectId = 1L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "notParticipantUser"
        when(jwtService.extractUsername(token)).thenReturn("notParticipantUser");

        // Act & Assert
        StepVerifier.create(projectService.isParticipantOfProjectOrError(projectId, token))
                .expectError(AccessException.class)
                .verify();
    }

    // isAdminOfProjectOrError
    @Test
    void isAdminOfProjectOrError_UserIsAdmin_ReturnsTrue() {
        // Arrange
        Long projectId = 1L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "adminUser"
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.isAdminOfProjectOrError(projectId, token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isAdminOfProjectOrError_UserIsNotAdmin_ReturnsError() {
        // Arrange
        Long projectId = 2L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "regularUser"
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.isAdminOfProjectOrError(projectId, token))
                .expectError(AccessException.class)
                .verify();
    }

    // findAllByUsername
    @Test
    void findAllByUsername_UserExists_ReturnsProjects() {
        StepVerifier.create(projectService.findAllByUsername("admin1"))
                .expectNextMatches(project -> "test_project1".equals(project.getName()))
                .expectNextMatches(project -> "test_project2".equals(project.getName()))
                .verifyComplete();
    }

    // findByUsernameAndId
    @Test
    void findByUsernameAndId_ProjectExists_ReturnsProject() {
        StepVerifier.create(projectService.findByUsernameAndId("admin1", 1L))
                .expectNextMatches(project -> "test_project1".equals(project.getName()))
                .verifyComplete();
    }

    @Test
    void findByUsernameAndId_ProjectDoesNotExist_ReturnsError() {
        StepVerifier.create(projectService.findByUsernameAndId("unknownUser", 999L))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Such project was not found!"))
                .verify();
    }

    // findById
    @Test
    void findById_ProjectExists_ReturnsProject() {
        // Проверяем, что метод возвращает проект, если он существует
        StepVerifier.create(projectService.findById(1L))
                .expectNextMatches(project -> project.getName().equals("test_project1"))
                .verifyComplete();
    }

    @Test
    void findById_ProjectNotExists_ThrowsNotFoundException() {
        // Проверяем, что метод выбрасывает ошибку, если проект не существует
        StepVerifier.create(projectService.findById(999L))  // ID, которого точно нет
                .expectError(NotFoundException.class)
                .verify();
    }

    // doFieldBelongsToProject
    @Test
    void doFieldBelongsToProject_FieldBelongsToProject_ReturnsTrue() {
        // Проверяем, что метод возвращает true, если поле принадлежит проекту
        StepVerifier.create(projectService.doFieldBelongsToProject(1L, 1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void doFieldBelongsToProject_FieldDoesNotBelongToProject_ReturnsFalse() {
        // Проверяем, что метод возвращает false, если поле не принадлежит проекту
        StepVerifier.create(projectService.doFieldBelongsToProject(1L, 2L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void doFieldBelongsToProject_FieldNotExists_ReturnsFalse() {
        // Проверяем, что метод возвращает false, если поле не существует
        StepVerifier.create(projectService.doFieldBelongsToProject(999L, 2L))
                .expectNext(false)
                .verifyComplete();
    }

    // findByIdAndVisibility
    @Test
    void findByIdAndVisibility_ProjectIsOpen_ReturnsProject() {
        // Arrange
        Long projectId = 1L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "regularUser"
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.findByIdAndVisibility(projectId, token))
                .expectNextMatches(project -> project.getName().equals("test_project1"))
                .verifyComplete();
    }

    @Test
    void findByIdAndVisibility_ProjectIsClosed_UserIsParticipant_ReturnsProject() {
        // Arrange
        Long projectId = 2L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "regularUser"
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.findByIdAndVisibility(projectId, token))
                .expectNextMatches(project -> project.getName().equals("test_project2"))
                .verifyComplete();
    }

    @Test
    void findByIdAndVisibility_ProjectIsClosed_UserIsNotParticipant_ThrowsAccessException() {
        // Arrange
        Long projectId = 2L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "regularUser"
        when(jwtService.extractUsername(token)).thenReturn("admin2");

        // Act & Assert
        StepVerifier.create(projectService.findByIdAndVisibility(projectId, token))
                .expectError(AccessException.class)
                .verify();
    }

    @Test
    void findByIdAndVisibility_ProjectNotFound_ThrowsNotFoundException() {
        // Arrange
        Long projectId = 999L;

        // Мокаем jwtService.extractUsername, чтобы вернуть "regularUser"
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.findByIdAndVisibility(projectId, token))
                .expectError(NotFoundException.class)
                .verify();
    }

}

