package com.morning.taskapimain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToProjectDTO {
    private Long roleId;
    private String username;
}
