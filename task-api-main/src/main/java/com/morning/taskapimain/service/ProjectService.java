package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.repository.FieldRepository;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectService{

    private final DatabaseClient client;
    private final ProjectRepository projectRepository;
    private final FieldRepository fieldRepository;
    private final JwtService jwtService;
    private static final String SELECT_QUERY =     """
    select p.id, p.name, p.description, p.status, p.created_at, p.updated_at, p.visibility from project  as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
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
                .flatMap(Project::fromMap);
    }

    public Mono<Project> findByUsernameAndId(String username, Long id) {
        String query = String.format("%s WHERE users.username = '%s' AND p.id = %s", SELECT_QUERY, username, id);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Project::fromMap);
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
        return projectRepository.findById(id);
    }

    public Mono<Project> findByIdAndVisibility(Long id, String token){
        return findById(id).flatMap(project -> {
            return project.getVisibility() == "OPEN" ?
                    Mono.just(project) :
                    findByUsernameAndId(jwtService.extractUsername(token), id);
        });
    }

    public Mono<Project> findByIdAndVisibility(Long id){
        return findById(id).flatMap(project -> {
            return project.getVisibility().equals("OPEN") ? Mono.just(project) : Mono.empty();
        });

    }

}
