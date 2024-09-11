package com.morning.taskapimain.mapper;

import com.morning.taskapimain.entity.Field;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.dto.FieldDTO;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FieldMapper {
    FieldDTO map(Field field);

    @InheritInverseConfiguration
    Field map(FieldDTO dto);
}
