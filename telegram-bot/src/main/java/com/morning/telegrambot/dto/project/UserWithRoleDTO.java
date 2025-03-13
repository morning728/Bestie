package com.morning.telegrambot.dto.project;

import lombok.Data;

@Data
public class UserWithRoleDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String roleName;
    private Long roleId;
}

