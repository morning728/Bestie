package com.morning.telegrambot.dto.project;


import lombok.Data;

@Data
public class ProjectStatusDTO {
    private Long id;
    private Long projectId;
    private String name;
    private String color;
}

