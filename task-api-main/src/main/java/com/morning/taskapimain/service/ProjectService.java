package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.entity.dto.UpdateProjectDTO;
import com.morning.taskapimain.entity.dto.UserWithRoleDTO;
import com.morning.taskapimain.entity.kafka.project.DeleteEvent;
import com.morning.taskapimain.entity.kafka.project.InviteEvent;
import com.morning.taskapimain.entity.project.*;
import com.morning.taskapimain.entity.user.Contacts;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.*;
import com.morning.taskapimain.repository.task.TaskAssigneeRepository;
import com.morning.taskapimain.repository.task.TaskRepository;
import com.morning.taskapimain.service.kafka.KafkaNotificationService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectResourceRepository projectResourceRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectTagRepository projectTagRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ProjectJWTService projectJWTService;
    private final KafkaNotificationService kafkaNotificationService;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final UserService userService;

    @Value("${application.invitation.url}")
    private String invitationUrl;
    /**
     * ✅ Проверка, есть ли у пользователя разрешение на действие в проекте
     */
    private Mono<Void> validateRequesterHasPermission(Long projectId, String token, Permission permission) {
        String requesterUsername = jwtService.extractUsername(token);

        return userRepository.findByUsername(requesterUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> projectRoleRepository.findRoleByProjectIdAndUserId(projectId, user.getId())
                        .switchIfEmpty(Mono.error(new AccessException("User does not have a role in this project!")))
                        .flatMap(role -> {
                            role.deserializePermissions();
                            // Проверяем наличие разрешения
                            if (Boolean.TRUE.equals(role.getPermissionsJson().get(permission))) {
                                return Mono.empty(); // Разрешение есть, продолжаем
                            } else {
                                return Mono.error(new AccessException("You don't have permission for this action!"));
                            }
                        })
                );
    }

    /**
     * ✅ Получение полной информации по проекту
     */
    public Mono<ProjectDTO> getFullProjectInfoById(Long projectId) {

        return getProjectById(projectId)
                .map(project -> new ProjectDTO(project))
                .flatMap(projectDTO -> {
                    Mono<List<ProjectRole>> rolesMono = getRolesByProjectId(projectId).collectList();
                    Mono<List<ProjectStatus>> statusesMono = getStatusesByProjectId(projectId).collectList();
                    Mono<List<UserWithRoleDTO>> membersMono = getAllUsersInProject(projectId).collectList();
                    Mono<List<ProjectTag>> tagsMono = getTagsByProjectId(projectId).collectList();
                    return Mono.zip(rolesMono, statusesMono, membersMono, tagsMono)
                            .map(tuple -> {
                                projectDTO.setRoles(tuple.getT1()); // ✅ Устанавливаем роли
                                projectDTO.setStatuses(tuple.getT2()); // ✅ Устанавливаем статусы
                                projectDTO.setMembers(tuple.getT3()); // ✅ Устанавливаем пользователей с ролями
                                projectDTO.setTags(tuple.getT4()); // ✅ Устанавливаем теги
                                return projectDTO;
                            });
                })
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));

    }

    /**
     * ✅ Создание проекта
     */
    public Mono<Project> createProject(Project project, String token) {
        String ownerUsername = jwtService.extractUsername(token);
        return userRepository.findByUsername(ownerUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(owner -> {
                    project.setOwnerId(owner.getId());
                    project.setCreatedAt(LocalDateTime.now());
                    project.setUpdatedAt(LocalDateTime.now());
                    return projectRepository.save(project)
                            .flatMap(savedProject ->
                                    projectRoleRepository.createDefaultRoles(savedProject.getId())
                                            .flatMap(projectRole -> projectUserRepository.assignUserToProject(savedProject.getId(), owner.getId(), projectRole.getId()))
                                            .thenReturn(savedProject)
                            );
                });
    }

    /**
     * ✅ Получение проекта по ID
     */
    public Mono<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));
    }

    /**
     * ✅ Обновление проекта с учетом тегов, статусов и ресурсов
     */
    public Mono<Project> updateProject(Long projectId, UpdateProjectDTO updatedProject, String token) {
        System.out.println(updatedProject.toString());
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectRepository.findById(projectId)
                        .switchIfEmpty(Mono.error(new NotFoundException("Project not found")))
                        .flatMap(existingProject -> {

                            // Обновляем основную информацию проекта
                            existingProject.setTitle(updatedProject.getTitle());
                            existingProject.setDescription(updatedProject.getDescription());
                            existingProject.setColor(updatedProject.getColor());
                            existingProject.setUpdatedAt(LocalDateTime.now());

                            // ⚡ Обновляем теги, статусы и ресурсы
                            return projectRepository.save(existingProject)
                                    .thenReturn(existingProject);
                        }));
    }


    /**
     * ✅
     */
    public Mono<ProjectTag> updateProjectTag(Long projectId, ProjectTag updatedTag, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectTagRepository.save(updatedTag));
    }

    /**
     * ✅
     */
    public Mono<ProjectTag> addProjectTag(Long projectId, ProjectTag tag, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectTagRepository.saveTag(projectId, tag.getName(), tag.getColor()));
    }

    /**
     * ✅
     */
    public Mono<Void> deleteProjectTag(Long projectId, Long tagId, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectTagRepository.deleteById(tagId));
    }

    /**
     * ✅
     */
    public Flux<ProjectTag> getProjectTags(Long projectId) {
        return projectTagRepository.findTagsByProjectId(projectId);
    }

    /**
     * ✅
     */
    public Mono<ProjectStatus> updateProjectStatus(Long projectId, ProjectStatus updatedStatus, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectStatusRepository.save(updatedStatus));
    }

    /**
     * ✅
     */
    public Mono<ProjectStatus> addProjectStatus(Long projectId, ProjectStatus newStatus, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectStatusRepository.saveStatus(projectId, newStatus.getName(), newStatus.getColor(), newStatus.getPosition()));
    }

    /**
     * ✅
     */
    public Flux<ProjectStatus> getProjectStatuses(Long projectId) {
        return projectStatusRepository.findStatusesByProjectId(projectId);
    }

    /**
     * ✅
     */
    public Mono<Void> deleteProjectStatus(Long projectId, Long statusId, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectStatusRepository.deleteById(statusId));
    }

    /**
     * ✅
     */
    public Mono<ProjectResource> updateProjectResources(Long projectId, ProjectResource updatedResource, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectResourceRepository.save(updatedResource));
    }

    /**
     * ✅
     */
    public Mono<ProjectResource> addProjectResource(Long projectId, ProjectResource newResource, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectResourceRepository.saveResource(projectId, newResource.getUrl(), newResource.getDescription()));
    }

    /**
     * ✅
     */
    public Mono<Void> deleteProjectResource(Long projectId, Long resourceId, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectResourceRepository.deleteById(resourceId));
    }

    /**
     * ✅
     */
    public Flux<ProjectResource> getProjectResources(Long projectId) {
        return projectResourceRepository.findResourcesByProjectId(projectId);
    }

    /**
     * ✅ Получение всех проектов пользователя
     */
    public Flux<Project> getUserProjects(String token) {
        return userRepository.findByUsername(jwtService.extractUsername(token))
                .flatMapMany(user -> projectRepository.findAllByUserId(user.getId()));
    }

    /**
     * ✅ Удаление проекта (только владелец)
     */
    public Mono<Void> deleteProject(Long projectId, String token) {
        String username = jwtService.extractUsername(token);
        return getUserId(username)
                .flatMap(userId -> projectRepository.findById(projectId)
                        .switchIfEmpty(Mono.error(new NotFoundException("Project not found")))
                        .flatMap(project -> {
                            if (!project.getOwnerId().equals(userId)) {
                                return Mono.error(new AccessException("You are not the owner of this project!"));
                            }
                            return projectRepository.delete(project);
                        }));
    }

    /**
     * ✅ Добавление пользователя в проект
     */
    public Mono<Void> addUserToProject(Long projectId, String username, Long roleId, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_MEMBERS)
                .then(userRepository.findByUsername(username)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                        .flatMap(user -> projectRoleRepository.findRoleById(roleId)
                                .switchIfEmpty(Mono.error(new NotFoundException("Role not found")))
                                .flatMap(role -> projectUserRepository.assignUserToProject(projectId, user.getId(), roleId)))
                );
    }

    /**
     * ✅ Удаление пользователя из проекта
     */
    public Mono<Void> removeUserFromProject(Long projectId, String username, String token) {


        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_MEMBERS)
                .then(userRepository.findByUsernameAndProjectId(username, projectId)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                        .flatMap(user -> projectUserRepository.removeUserFromProject(projectId, user.getId()))
                        .then(projectRepository.findProjectTitleById(projectId))
                        .flatMap(projectTitle -> {
                            String deletedByUsername = jwtService.extractUsername(token);
                            DeleteEvent deleteEvent = DeleteEvent.builder()
                                    .username(username)
                                    .projectTitle(projectTitle)
                                    .deletedBy(deletedByUsername)
                                    .build()
                                    .setDefaultAction();

                            return kafkaNotificationService.sendDeleteFromProject(deleteEvent);
                        })
                );
    }


    /**
     * ✅ Смена роли пользователя в проекте
     */
    public Mono<Void> changeUserRole(Long projectId, String username, Long newRoleId, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_ROLES)
                .then(userRepository.findByUsernameAndProjectId(username, projectId)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                        .flatMap(user -> projectRoleRepository.findRoleById(newRoleId)
                                .switchIfEmpty(Mono.error(new NotFoundException("Role does not exist")))
                                .flatMap(role -> projectUserRepository.updateUserRole(projectId, user.getId(), newRoleId))
                        )
                );
    }


    /**
     * ✅ Проверка, является ли пользователь владельцем проекта
     */
    public Mono<Boolean> isProjectOwner(Long projectId, String token) {
        return getUserId(jwtService.extractUsername(token))
                .flatMap(userId -> projectRepository.findById(projectId)
                        .map(project -> project.getOwnerId().equals(userId))
                        .defaultIfEmpty(false));
    }

    /**
     * ✅ Вспомогательный метод для получения ID пользователя
     */
    private Mono<Long> getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    public Mono<ProjectRole> addRole(Long projectId, ProjectRole projectRole, String token) {
        projectRole.serializePermissions();
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectRoleRepository.addRole(projectId, projectRole.getName(), projectRole.getPermissions()));
    }


    public Mono<ProjectRole> updateRole(Long projectId, ProjectRole projectRole, String token) {
        projectRole.serializePermissions();
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectRoleRepository.updateRole(projectId, projectRole.getId(), projectRole.getName(), projectRole.getPermissions()));
    }

    public Mono<Void> deleteRole(Long projectId, Long projectRoleId, String token) {
        String requesterUsername = jwtService.extractUsername(token);

        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(userRepository.findByUsername(requesterUsername)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found"))))
                .flatMap(user -> projectRoleRepository.countRolesByProjectId(projectId)
                        .flatMap(roleCount -> {
                            if (roleCount <= 1) {
                                return Mono.error(new BadRequestException("Cannot delete the only role in the project!"));
                            }
                            return projectRoleRepository.findRoleByProjectIdAndUserId(projectId, user.getId());
                        })
                        .flatMap(userRole -> {
                            return projectRoleRepository.countUsersWithRole(projectRoleId)
                                    .flatMap(userCount -> {
                                        if (userCount > 0) {
                                            return Mono.error(new BadRequestException("Cannot delete a role that is assigned to users!"));
                                        }
                                        return projectRoleRepository.deleteRole(projectRoleId);
                                    });
                        })
                );
    }

    /**
     * ✅ Получение всех пользователей в проекте с их ролями
     */
    public Flux<UserWithRoleDTO> getAllUsersInProject(Long projectId) {
        return projectUserRepository.findUsersWithRolesByProjectId(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));
    }

    public Flux<ProjectRole> getRolesByProjectId(Long projectId) {
        return (projectRoleRepository.getRolesByProjectId(projectId)
                .flatMap(projectRole -> {
                    projectRole.deserializePermissions();
                    return Mono.just(projectRole);
                }));
    }

    public Flux<ProjectStatus> getStatusesByProjectId(Long projectId) {
        return (projectStatusRepository.findStatusesByProjectId(projectId));
    }

    public Flux<ProjectTag> getTagsByProjectId(Long projectId) {
        return (projectTagRepository.findTagsByProjectId(projectId));
    }

    public Mono<ProjectRole> getMyRoleByProjectId(Long projectId, String token) {
        return getUserId(jwtService.extractUsername(token))
                .flatMap(userId -> projectRoleRepository.findRoleByProjectIdAndUserId(projectId, userId)
                        .flatMap(projectRole -> {
                            projectRole.deserializePermissions();
                            return Mono.just(projectRole);
                        }));
    }

    public Mono<String> generateInviteToken(String token, Long projectId, String username, Long roleId) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_MEMBERS)
                .then(Mono.fromSupplier(() -> {
                    Map<String, String> claims = new HashMap<>();
                    claims.put("projectId", String.valueOf(projectId));
                    claims.put("roleId", String.valueOf(roleId));
                    return projectJWTService.buildInviteToken(claims, username);
                }));
    }

    public Mono<String> generateInviteAllToken(String token, Long projectId, Long roleId) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_MEMBERS)
                .then(Mono.fromSupplier(() -> {
                    Map<String, String> claims = new HashMap<>();
                    claims.put("projectId", String.valueOf(projectId));
                    claims.put("roleId", String.valueOf(roleId));
                    return projectJWTService.buildInviteToken(claims, "public_invite");
                }));
    }

    public Mono<Void> processInviteToken(String invitationToken, String token) {
        if (projectJWTService.isTokenExpired(invitationToken)) {
            return Mono.error(new AccessException("Invitation token is expired"));
        }
        String username = projectJWTService.extractUsername(invitationToken);
        Long projectId = projectJWTService.extractProjectId(invitationToken);
        Long roleId = projectJWTService.extractRoleId(invitationToken);

        Mono<User> user = userRepository.findByUsername(jwtService.extractUsername(token))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
        Mono<Project> project = projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));
        Mono<ProjectRole> role = projectRoleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new NotFoundException("Role not found")));

        return Mono.zip(user, project, role)
                .flatMap(tuple -> {
                    Long userId = tuple.getT1().getId();
                    String tokenOwnerUsername = tuple.getT1().getUsername();
                    if (!tokenOwnerUsername.equals(username)) {
                        return Mono.error(new AccessException("You were not invited!"));
                    }
                    return projectUserRepository.assignUserToProject(projectId, userId, roleId);
                }).then();
    }

    public Mono<Void> processInviteAllToken(String invitationToken, String token) {
        if (projectJWTService.isTokenExpired(invitationToken)) {
            return Mono.error(new AccessException("Invitation token is expired"));
        }
        String username = projectJWTService.extractUsername(invitationToken);
        Long projectId = projectJWTService.extractProjectId(invitationToken);
        Long roleId = projectJWTService.extractRoleId(invitationToken);

        Mono<User> user = userRepository.findByUsername(jwtService.extractUsername(token))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
        Mono<Project> project = projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));
        Mono<ProjectRole> role = projectRoleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new NotFoundException("Role not found")));

        return Mono.zip(user, project, role)
                .flatMap(tuple -> {
                    Long userId = tuple.getT1().getId();
                    return projectUserRepository.assignUserToProject(projectId, userId, roleId);
                }).then();
    }

    public Mono<Void> inviteDirectly(String token, Long projectId, String username, Long roleId) {
        Mono<User> invitedBy = userRepository.findByUsername(jwtService.extractUsername(token));
        Mono<Project> project = projectRepository.findById(projectId);
        Mono<String> invitationTokenMono = generateInviteToken(token, projectId, username, roleId);
        return Mono.zip(invitedBy, project, invitationTokenMono)
                .flatMap(tuple -> {
                    String invitedByUsername = tuple.getT1().getUsername();
                    String projectTitle = tuple.getT2().getTitle();
                    InviteEvent inviteEvent = InviteEvent.builder()
                            .username(username)
                            .inviteLink(invitationUrl.concat(tuple.getT3()))
                            .projectTitle(projectTitle)
                            .invitedBy(invitedByUsername)
                            .build()
                            .setDefaultAction();
                    return kafkaNotificationService.sendInviteToProject(inviteEvent);
                });
    }

}
