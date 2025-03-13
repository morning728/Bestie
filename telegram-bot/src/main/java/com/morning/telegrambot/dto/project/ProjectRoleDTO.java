package com.morning.telegrambot.dto.project;


import lombok.Data;

@Data
public class ProjectRoleDTO {
    private Long id;
    private Long projectId;
    private String name;
    private String permissions; // JSON строка с правами
}

