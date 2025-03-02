/*
package com.morning.taskapimain.integration;

import com.morning.taskapimain.Init.PostgresDatabaseContainerInitializer;
import com.morning.taskapimain.entity.dto.FieldDTO;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    // addProject
    @Test
    void addProject_SuccessfulAddition_ReturnsProject() {
        // Arrange
        String username = "admin2";
        ProjectDTO projectDTO = ProjectDTO.builder()
                .id(123L)
                .name("New Project")
                .description("Description")
                .visibility("CLOSE")
                .build();
        when(jwtService.extractUsername(token)).thenReturn(username);

        // Act & Assert
        StepVerifier.create(projectService.addProject(projectDTO, token))
                .assertNext(project -> {
                    assertNotNull(project.getId());
                    assertEquals("New Project", project.getName());
                    assertEquals("Description", project.getDescription());
                })
                .verifyComplete();
        StepVerifier.create(projectService.findAllByUsername(username))
                .expectNextMatches(project -> "New Project".equals(project.getName()))
                .verifyComplete();
    }

    @Test
    void addProject_UserNotFound_ThrowsNotFoundException() {
        // Arrange
        String username = "nonExistentUser";
        ProjectDTO projectDTO = ProjectDTO.builder()
                .id(123L)
                .name("New Project")
                .description("Description")
                .visibility("CLOSE")
                .build();

        when(jwtService.extractUsername(token)).thenReturn(username);

        // Act & Assert
        StepVerifier.create(projectService.addProject(projectDTO, token))
                .expectError(NotFoundException.class)
                .verify();
    }

    // deleteProjectById
    @Test
    void deleteProjectById_ProjectExists_DeletesSuccessfully() {
        // Arrange
        Long projectId = 4L;
        String username = "admin3";

        when(jwtService.extractUsername(token)).thenReturn(username);

        // Act & Assert
        StepVerifier.create(projectService.deleteProjectById(projectId, token))
                .verifyComplete();
        StepVerifier.create(projectService.findByUsernameAndId(username, projectId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void deleteProjectById_ProjectNotFound_ThrowsNotFoundException() {
        // Arrange
        Long projectId = 999L;  // Несуществующий ID
        String username = "admin3";

        when(jwtService.extractUsername(token)).thenReturn(username);

        // Act & Assert
        StepVerifier.create(projectService.deleteProjectById(projectId, token))
                .expectError(NotFoundException.class)
                .verify();
    }

    // updateProject
    @Test
    void updateProject_ProjectExists_UpdatesSuccessfully() {
        // Arrange
        Long projectId = 3L;
        String username = "admin3";
        ProjectDTO projectDTO = ProjectDTO.builder()
                .id(projectId)
                .name("Updated Project")
                .description("UPD Description")
                .visibility("OPEN")
                .build();

        when(jwtService.extractUsername(token)).thenReturn(username);

        // Act & Assert
        StepVerifier.create(projectService.updateProject(projectDTO, token))
                .assertNext(updatedProject -> {
                    assertEquals("Updated Project", updatedProject.getName());
                    assertEquals("UPD Description", updatedProject.getDescription());
                    assertEquals("OPEN", updatedProject.getVisibility());
                })
                .verifyComplete();
    }

    @Test
    void updateProject_ProjectNotFound_ThrowsNotFoundException() {
        // Arrange
        Long nonExistentProjectId = 999L;  // Несуществующий ID
        String username = "admin3";
        ProjectDTO projectDTO = ProjectDTO.builder()
                .id(nonExistentProjectId)
                .name("Updated Project")
                .description("UPD Description")
                .visibility("OPEN")
                .build();

        when(jwtService.extractUsername(token)).thenReturn(username);

        // Act & Assert
        StepVerifier.create(projectService.updateProject(projectDTO, token))
                .expectError(NotFoundException.class)
                .verify();
    }
    //findProjectFieldsByProjectId
    @Test
    void findProjectFieldsByProjectId_WithAccess_ReturnsFields() {
        // Arrange
        Long projectId = 1L;
        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.findProjectFieldsByProjectId(projectId, token))
                .expectNextMatches(field -> field.getProjectId().equals(projectId))
                .verifyComplete();
    }

    @Test
    void findProjectFieldsByProjectId_WithoutAccess_ThrowsAccessException() {
        // Arrange
        Long projectId = 1L;
        when(jwtService.extractUsername(token)).thenReturn("userWithNoAccess");

        // Act & Assert
        StepVerifier.create(projectService.findProjectFieldsByProjectId(projectId, token))
                .expectError(AccessException.class)
                .verify();
    }

    // addField
    @Test
    void addField_AdminHasAccess_AddsField() {
        // Arrange
        Long projectId = 3L;
        FieldDTO fieldDTO = FieldDTO.builder()
                .projectId(projectId)
                .name("Test Field")
                .build();

        when(jwtService.extractUsername(token)).thenReturn("admin3");

        // Act & Assert
        StepVerifier.create(projectService.addField(fieldDTO, token))
                .assertNext(field -> {
                    assertEquals("Test Field", field.getName());
                    assertEquals(projectId, field.getProjectId());
                })
                .verifyComplete();
        StepVerifier.create(projectService.findProjectFieldsByProjectId(projectId, token))
                .expectNextMatches(field -> "Test Field".equals(field.getName()))
                .verifyComplete();
    }

    @Test
    void addField_NonAdmin_ThrowsAccessException() {
        // Arrange
        Long projectId = 4L;
        FieldDTO fieldDTO = FieldDTO.builder()
                .projectId(projectId)
                .name("Test Field")
                .build();

        when(jwtService.extractUsername(token)).thenReturn("admin3");

        // Act & Assert
        StepVerifier.create(projectService.addField(fieldDTO, token))
                .expectError(AccessException.class)
                .verify();
    }

    @Test
    void updateField_FieldExists_UpdatesSuccessfully() {
        // Arrange
        Long projectId = 1L;
        FieldDTO fieldDTO = FieldDTO.builder()
                .id(1L)
                .projectId(projectId)
                .name("Updated Field")
                .build();

        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.updateField(fieldDTO, token))
                .assertNext(updatedField -> {
                    assertEquals("Updated Field", updatedField.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateField_FieldNotFound_ThrowsNotFoundException() {
        // Arrange
        Long projectId = 1L;
        Long nonExistentFieldId = 999L;
        FieldDTO fieldDTO = FieldDTO.builder()
                .id(nonExistentFieldId)
                .projectId(projectId)
                .name("Updated Field")
                .build();

        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.updateField(fieldDTO, token))
                .expectError(NotFoundException.class)
                .verify();
    }

    // deleteFieldById
    @Test
    void deleteFieldById_AdminHasAccess_DeletesField() {
        // Arrange
        Long fieldId = 1L;
        Long projectId = 1L;

        when(jwtService.extractUsername(token)).thenReturn("admin1");

        // Act & Assert
        StepVerifier.create(projectService.deleteFieldById(fieldId, token))
                .verifyComplete();
    }

    @Test
    void deleteFieldById_FieldNotFound_ThrowsNotFoundException() {
        // Arrange
        Long fieldId = 999L;

        // Act & Assert
        StepVerifier.create(projectService.deleteFieldById(fieldId, token))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void deleteFieldById_NonAdminUser_ThrowsAccessException() {
        // Arrange
        Long fieldId = 3L;
        Long projectId = 4L;

        when(jwtService.extractUsername(token)).thenReturn("admin3");

        // Act & Assert
        StepVerifier.create(projectService.deleteFieldById(fieldId, token))
                .expectError(AccessException.class)
                .verify();
    }

}

*/
