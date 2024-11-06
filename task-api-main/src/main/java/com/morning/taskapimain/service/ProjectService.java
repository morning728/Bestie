package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.FieldDTO;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.FieldRepository;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.service.kafka.KafkaNotificationService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService{

    private final DatabaseClient client;
    private final ProjectRepository projectRepository;
    private final FieldRepository fieldRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final JwtEmailService jwtEmailService;
    private final KafkaNotificationService kafkaNotificationService;

    private static final String SELECT_QUERY =     """
    select p.id, p.name, p.description, p.status, p.created_at, p.updated_at, p.visibility from project  as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;

    private static final String SELECT_USERS_BY_PROJECT_ID_QUERY =     """
    select users.id, users.username, users.first_name,users.last_name, users.status, users.created_at, users.updated_at, user_project.role
     from project  as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;

    private static final String SELECT_ROLE_BY_PROJECT_ID_QUERY =     """
    select user_project.role
     from project  as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;

    private static final String ADD_USER_TO_PROJECT =     """
    INSERT INTO public.user_project (project_id, user_id) VALUES (%s, %s)
    """;
    private static final String DELETE_USER_FROM_PROJECT =     """
    DELETE FROM public.user_project WHERE project_id = %s AND user_id = %s
    """;
    private static final String INSERT_PROJECT_USER_QUERY =     """
    INSERT INTO public.user_project (project_id, user_id, role) VALUES (%s, %s, 'ADMIN')
    """;

    private static final String CHANGE_USER_ROLE =     """
    UPDATE public.user_project SET role = '%s' WHERE project_id = %s AND user_id = %s
    """;



    /*Проверяет, является ли запрашивающий юзер участником проекта
     TESTED*/
    public Mono<Boolean> isParticipantOfProjectOrError(Long projectId, String token) {
        String username = jwtService.extractUsername(token);

        String query = SELECT_QUERY + " WHERE users.username = :username AND p.id = :projectId";

        return client.sql(query)
                .bind("username", username)
                .bind("projectId", projectId)
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .switchIfEmpty(Mono.error(new AccessException("You are not a participant of the project!")))
                .map(project ->  (true));
    }


    public Mono<Boolean> isManagerOfProjectOrError(Long projectId, String token){
        String username = jwtService.extractUsername(token);
        String query = String.format(
                "%s WHERE users.username = '%s' AND p.id = %s AND user_project.role = 'MANAGER'",
                SELECT_QUERY,
                username,
                projectId
        );
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> project.isEmpty() ?
                        Mono.error(new AccessException("You are not manager of project!")) :
                        Mono.just(true));
    }
    /*TESTED*/
    public Mono<Boolean> isAdminOfProjectOrError(Long projectId, String token) {
        String username = jwtService.extractUsername(token);

        // Создание SQL-запроса с использованием параметров
        String query = String.format(
                "%s WHERE users.username = :username AND p.id = :projectId AND user_project.role = 'ADMIN'",
                SELECT_QUERY
        );

        return client.sql(query)
                .bind("username", username) // Используем параметр для username
                .bind("projectId", projectId) // Используем параметр для projectId
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .switchIfEmpty(Mono.error(new AccessException("You are not an admin of the project!"))) // Уточненное сообщение
                .map(project -> true);
    }


    /*Проверяет, имеет ли запрашивающий доступи к проекту*/
    public Mono<Boolean> hasAccessToProjectOrError(Long projectId, String token){
        return findById(projectId).defaultIfEmpty(Project.defaultIfEmpty()).flatMap(project -> {
            if(project.isEmpty())
                return Mono.error(new NotFoundException("Project was not found!"));
            return project.getVisibility() == "OPEN" ?
                    Mono.just(true) :
                    findByUsernameAndId(jwtService.extractUsername(token), projectId)
                            .defaultIfEmpty(Project.defaultIfEmpty())
                            .onErrorReturn(Project.defaultIfEmpty())
                            .flatMap(project1 -> project1.isEmpty() ?
                                    Mono.error(new AccessException("You have not access to project!")) :
                                    Mono.just(true));
        });
    }
    public Flux<Project> findAllByUserId(Long id) {
        String query = String.format("%s WHERE users.id =%s", SELECT_QUERY, id);
        return client.sql(query)
                .fetch()
                .all()
                .flatMap(Project::fromMap);
    }

    /*tested*/
    public Flux<Project> findAllByUsername(String username) {
        String query = String.format("%s WHERE users.username = :username", SELECT_QUERY);

        return client.sql(query)
                .bind("username", username) // Используем параметр
                .fetch()
                .all()
                .flatMap(Project::fromMap)
                .switchIfEmpty(Flux.error(new NotFoundException("No one project was found!"))); // Упрощаем обработку ошибки
    }

    /*tested*/
    public Mono<Project> findByUsernameAndId(String username, Long id) {
        String query = String.format("%s WHERE users.username = :username AND p.id = :id", SELECT_QUERY);

        return client.sql(query)
                .bind("username", username) // Параметр для username
                .bind("id", id) // Параметр для id
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .switchIfEmpty(Mono.error(new NotFoundException("Such project was not found!"))); // Упрощенная обработка ошибки
    }


    /*tested*/
    public Mono<Boolean> doFieldBelongsToProject(Long fieldId, Long projectId) {
        return fieldRepository.findById(fieldId)
                .filter(field -> field.getProjectId().equals(projectId))
                .hasElement();
    }

    /*tested*/
    public Mono<Project> findById(Long id) {
        return projectRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Project was not found!"))));
    }


    /*Выдает проект всем, если он открыт и только участникам в противном случае*/
/*    public Mono<Project> findByIdAndVisibility(Long id, String token){
        return findById(id).defaultIfEmpty(Project.defaultIfEmpty()).flatMap(project -> {
            if(project.isEmpty())
                return Mono.error(new NotFoundException("Project was not found!"));
            return project.getVisibility() == "OPEN" ?
                    Mono.just(project) :
                    findByUsernameAndId(jwtService.extractUsername(token), id);
        });
    }*/
    /*Выдает проект всем, если он открыт и только участникам в противном случае
    tested*/
    public Mono<Project> findByIdAndVisibility(Long id, String token) {
        return findById(id)
                .flatMap(project -> {
                    if ("OPEN".equals(project.getVisibility())) {
                        return Mono.just(project);
                    }
                    // Закрытый проект — проверяем пользователя
                    String username = jwtService.extractUsername(token);
                    return findByUsernameAndId(username, id)
                            .onErrorMap(NotFoundException.class, ex -> new AccessException("Access denied!"));
                })
                .switchIfEmpty(Mono.error(new NotFoundException("Such project was not found!")));
    }


    public Mono<Project> findByIdAndVisibility(Long id){
        return findById(id).flatMap(project -> {
            if(!project.isEmpty()){
                return Mono.just(project);
            }
            return Mono.error(new NotFoundException("Project was not found!"));
        });
    }

    /*Добавляет проект в бд, а также привязывает проект к юзеру, который отправил запрос на создание*/
    public Mono<Project> addProject(ProjectDTO dto, String token){
        String username = jwtService.extractUsername(token);
        return projectRepository.save(dto.toInsertProject()).flatMap(project -> {
            return userService.findUserByUsername(username).flatMap(user -> {
                String query = String.format(INSERT_PROJECT_USER_QUERY, project.getId(),user.getId());
                return client.sql(query)
                        .fetch()
                        .first()
                        .flatMap(stringObjectMap -> Mono.just(project));
            });
        });
    }

    public Mono<Void> deleteProjectById(Long id, String token){
        String username = jwtService.extractUsername(token);
        return findByUsernameAndId(username, id)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> {
                    if(!project.isEmpty()){
                        return projectRepository.deleteById(id);
                    }
                    return Mono.error(new NotFoundException("Project was not found!"));
                });
    }

    /*Метод обновления проекта: если у пользователя, отправившего запрос, есть такой проект, то обновляем его*/
    public Mono<Project> updateProject(ProjectDTO dto, String token){
        String username = jwtService.extractUsername(token);
        return findByUsernameAndId(username, dto.getId())
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> {
                    if(!project.isEmpty()){
                        project.update(dto.toProject());
                        return projectRepository.save(project);
                    }
                    return Mono.error(new NotFoundException("Project was not found!"));
                });
    }

    public Flux<Field> findProjectFieldsByProjectId(Long projectId, String token){
        return hasAccessToProjectOrError(projectId, token)
                .thenMany(fieldRepository.findByProjectIdOrderById(projectId));
    }

    public Mono<Field> addField(FieldDTO dto, String token){ /////////////////////////////////////test
        String username = jwtService.extractUsername(token);
        return isAdminOfProjectOrError(dto.getProjectId(), token)
                .then(fieldRepository.save(dto.map()));
    }

    public Mono<Field> updateField(FieldDTO dto, String token){
    isAdminOfProjectOrError(dto.getProjectId(), token);
    return fieldRepository.findById(dto.getId())
            .defaultIfEmpty(Field.defaultIfEmpty())
            .flatMap(field -> {
                if(!field.isEmpty()){
                    return isParticipantOfProjectOrError(dto.getProjectId(), token)
                            .then(fieldRepository.save(dto.map()));
                }
                return Mono.error(new NotFoundException("Field was not found!"));
            });
    }

    public Mono<Void> deleteFieldById(Long fieldId, String token){
    return fieldRepository.findById(fieldId)
            .defaultIfEmpty(Field.defaultIfEmpty())
            .flatMap(field -> {
                if(!field.isEmpty()){
                    return isAdminOfProjectOrError(field.getProjectId(), token)
                            .then(fieldRepository.deleteById(fieldId));
                }
                return Mono.error(new NotFoundException("Field was not found!"));
            });
    }

    public Flux<UserDTO> findUsersByProjectId(Long projectId, String token){
        String query =  String.format("%s WHERE p.id = '%s' order by users.id", SELECT_USERS_BY_PROJECT_ID_QUERY, projectId);
        return hasAccessToProjectOrError(projectId, token)
                .thenMany(
                    client.sql(query)
                            .fetch()
                            .all()
                            .flatMap(stringObjectMap -> {
                                User user = User.fromMap(stringObjectMap);
                                UserDTO dto = UserDTO.fromUser(user);
                                dto.setRole((String) stringObjectMap.get("role"));
                                return Mono.just(dto);
                            })
                );
    }
    public Flux<Object> addUserToProjectByAcceptationToken(String acceptationToken){
        Long projectId = jwtEmailService.extractProjectId(acceptationToken);
        String username = jwtEmailService.extractUsername(acceptationToken);
        return addUserToProject(projectId, username);
    }
    public Flux<Object> addUserToProject(Long projectId, String username) {
        //String query =  String.format(ADD_USER_TO_PROJECT, projectId, userId);

        return userService.findUserByUsername(username)
            .defaultIfEmpty(User.defaultIfEmpty())
            .flatMap(user -> {
                if(user.isEmpty()){
                    return Mono.error(new NotFoundException("User you want to add does not exist!"));
                }
                return  Mono.just(user.getId());
            })
            .flatMapMany(userId ->{
                return client.sql(String.format(ADD_USER_TO_PROJECT, projectId, userId))
                        .fetch()
                        .first()
                        .onErrorResume(e -> Mono.error(new BadRequestException("Bad Request!")))
                        .flatMap(stringObjectMap -> {
                            return Mono.just(null);
                        });
            });

    }
    public Mono<Void> inviteUserToProject(Long projectId, String toUsername, String token) {
        //String query =  String.format(ADD_USER_TO_PROJECT, projectId, userId);

        String fromUsername = jwtService.extractUsername(token);


        return isAdminOfProjectOrError(projectId, token).then(
                userService.findProfileByUsername(toUsername, token)
                        .flatMap(profileDTO -> {
                            return kafkaNotificationService.sendInviteToProject(projectId,fromUsername, profileDTO.getUsername(), profileDTO.getEmail());
                        })
        );
    }

    public Flux<UserDTO> deleteUserFromProject(Long projectId, String usernameTo, String token) {//
        String from = jwtService.extractUsername(token);
        Mono<String> projectName = projectRepository.findById(projectId).flatMap(project -> Mono.just(project.getName()));
        Mono<ProfileDTO> toProfile = userService.findProfileByUsername(usernameTo, token);
        Mono.zip(projectName, toProfile).subscribe(result -> {
            kafkaNotificationService.sendDeleteNotification(result.getT1(), from, result.getT2().getUsername(), result.getT2().getEmail());
            String query =  String.format(DELETE_USER_FROM_PROJECT, projectId, result.getT2().getId());
            isAdminOfProjectOrError(projectId, token).thenMany(
                    client.sql(query)
                            .fetch()
                            .first()
                            .onErrorResume(e -> Mono.error(new BadRequestException("Bad Request!"))))
                            .subscribe(stringObjectMap -> System.out.println(stringObjectMap));
        }
        );
        return findUsersByProjectId(projectId, token);
    }

    public Mono<Object> changeUserRole(Long projectId, String username, String newRole, String token){

        Mono<Boolean> isAdmin = UserService.getUserRoleInProject(projectId, token, jwtService, client)
                .map(s -> s.equals("ADMIN"));

        Mono<Long> userId = userService.findUserByUsername(username)
                .map(user -> (user.getId()));

        return Mono.zip(isAdmin, userId)
                .flatMap(result -> {
                    if(result.getT1()){
                        String query =  String.format(CHANGE_USER_ROLE, newRole, projectId, result.getT2());
                        return client.sql(query)
                                .fetch()
                                .first()
                                .map(stringObjectMap -> "User's role was changed!")
                                .defaultIfEmpty("User's role was changed!")
                                .onErrorResume(e -> Mono.error(new BadRequestException("Bad Request!")));
                    } else {
                        return Mono.error(new AccessException("You Can't change user's role!"));
                    }
                });
    }

}
