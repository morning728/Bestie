package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.repository.ProjectRepository;
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
    private static final String SELECT_QUERY =     """
    select p.id, p.name, p.description, p.status, p.created_at, p.updated_at from project  as p
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
        String query = String.format("%s WHERE users.id = %s", SELECT_QUERY, id);
        return client.sql(query)
                .fetch()
                .all()
                .flatMap(Project::fromMap);
    }

    public Mono<Project> findById(Long id){
        return projectRepository.findById(id);
    }

}
