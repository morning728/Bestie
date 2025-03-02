package com.morning.taskapimain.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectRoleDTO {
    private Long id;
    private Long projectId;
    private String name;
    private Boolean canEditTasks;
    private Boolean canDeleteTasks;
    private Boolean canAddMembers;
    private Boolean canManageRoles;
}
