package com.morning.openrequesthandler.repository;

import com.morning.openrequesthandler.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

  Optional<Project> findById(Integer id);

}
