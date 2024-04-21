package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.dto.FieldDTO;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.mapper.FieldMapper;
import com.morning.taskapimain.mapper.ProjectMapper;
import com.morning.taskapimain.repository.FieldRepository;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.BooleanEvaluator;
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
    private final ProjectMapper projectMapper;

    private static final String SELECT_QUERY =     """
    select p.id, p.name, p.description, p.status, p.created_at, p.updated_at, p.visibility from project  as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;
    private static final String INSERT_PROJECT_USER_QUERY =     """
    INSERT INTO public.user_project (project_id, user_id) VALUES (%s, %s)
    """;

//    """
//            SELECT d.id d_id, d.name d_name, m.id m_id, m.first_name m_firstName, m.last_name m_lastName,
//                m.position m_position, m.is_full_time m_isFullTime, e.id e_id, e.first_name e_firstName,
//                e.last_name e_lastName, e.position e_position, e.is_full_time e_isFullTime
//            FROM departments d
//            LEFT JOIN department_managers dm ON dm.department_id = d.id
//            LEFT JOIN employees m ON m.id = dm.employee_id
//            LEFT JOIN department_employees de ON de.department_id = d.id
//            LEFT JOIN employees e ON e.id = de.employee_id
//            """;

    /*Проверяет, является ли запрашивающий юзер участником проекта*/
    public Mono<Boolean> isOwnerOfProjectOrError(Long projectId, String token){
        String username = jwtService.extractUsername(token);
        String query = String.format("%s WHERE users.username = '%s' AND p.id = %s", SELECT_QUERY, username, projectId);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> project.isEmpty() ?
                        Mono.error(new AccessException("You are not owner of project!")) :
                        Mono.just(true));
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

    public Flux<Project> findAllByUsername(String username) {
        String query = String.format("%s WHERE users.username = '%s'", SELECT_QUERY, username);
        return client.sql(query)
                .fetch()
                .all()
                .flatMap(Project::fromMap)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> project.isEmpty() ? Mono.error(new NotFoundException("No one project was found!")) : Mono.just(project));
    }

    public Mono<Project> findByUsernameAndId(String username, Long id) {
        String query = String.format("%s WHERE users.username = '%s' AND p.id = %s", SELECT_QUERY, username, id);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> project.isEmpty() ? Mono.error(new NotFoundException("Such project was not found!")) : Mono.just(project));
    }

    public Mono<Boolean> isFieldBelongsToProject(Long fieldId, Long projectId){
        Mono<Field> fieldMono = fieldRepository.findById(fieldId);
        return fieldMono
                .defaultIfEmpty(Field.defaultIfEmpty())
                .flatMap(field -> {
            if(field.getId() != -1L && field.getProjectId() == projectId){
                return Mono.just(true);
            }
            return Mono.just(false);
        });
    }


    public Mono<Project> findById(Long id){
        return projectRepository.findById(id)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> project.isEmpty() ? Mono.error(new NotFoundException("Project was not found!")) : Mono.just(project));
    }

    /*Выдает проект всем, если он открыт и только участникам в противном случае*/
    public Mono<Project> findByIdAndVisibility(Long id, String token){
        return findById(id).defaultIfEmpty(Project.defaultIfEmpty()).flatMap(project -> {
            if(project.isEmpty())
                return Mono.error(new NotFoundException("Project was not found!"));
            return project.getVisibility() == "OPEN" ?
                    Mono.just(project) :
                    findByUsernameAndId(jwtService.extractUsername(token), id);
        });
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
                        project.update(dto);
                        return projectRepository.save(project);
                    }
                    return Mono.error(new NotFoundException("Project was not found!"));
                });
    }

    public Flux<Field> findProjectFieldsByProjectId(Long projectId, String token){
        return hasAccessToProjectOrError(projectId, token)
                .thenMany(fieldRepository.findByProjectIdOrderById(projectId));
    }

    public Mono<Field> addField(FieldDTO dto, String token){
        String username = jwtService.extractUsername(token);
        isOwnerOfProjectOrError(dto.getProjectId(), token);
        return fieldRepository.save(dto.map());
    }

        public Mono<Field> updateField(FieldDTO dto, String token){
        isOwnerOfProjectOrError(dto.getProjectId(), token);
        return fieldRepository.findById(dto.getId())
                .defaultIfEmpty(Field.defaultIfEmpty())
                .flatMap(field -> {
                    if(!field.isEmpty()){
                        return isOwnerOfProjectOrError(dto.getProjectId(), token)
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
                        return isOwnerOfProjectOrError(field.getProjectId(), token)
                                .then(fieldRepository.deleteById(fieldId));
                    }
                    return Mono.error(new NotFoundException("Field was not found!"));
                });
    }

}
