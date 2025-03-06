package com.morning.taskapimain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithRoleDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String roleName;
    private Long roleId;
}
