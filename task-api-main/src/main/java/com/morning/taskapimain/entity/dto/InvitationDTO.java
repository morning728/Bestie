package com.morning.taskapimain.entity.dto;

import lombok.Data;

@Data
public class InvitationDTO {
    private String username;
    private Long projectId;
    private Long roleId;
}
