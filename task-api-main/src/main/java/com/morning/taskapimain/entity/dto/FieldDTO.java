package com.morning.taskapimain.entity.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.morning.taskapimain.entity.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FieldDTO extends Field {
    public Field map(){
        return Field.builder()
                .id(getId() == null ? null : getId())
                .name(getName() == null ? null : getName())
                .projectId(getProjectId() == null ? null : getProjectId())
                .build();
    }

}
