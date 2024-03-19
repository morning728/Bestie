package com.morning.taskapimain.mapper;

import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO map(Project project);

    @InheritInverseConfiguration
    Project map(ProjectDTO dto);
}
