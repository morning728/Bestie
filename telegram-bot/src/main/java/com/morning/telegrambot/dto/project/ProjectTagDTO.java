package com.morning.telegrambot.dto.project;


import lombok.Data;

@Data
public class ProjectTagDTO {
    private Long id;
    private Long projectId;
    private String name;
    private String color;
}

