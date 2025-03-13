package com.morning.telegrambot.dto;

import lombok.Data;

@Data
public class ProjectTagDTO {
    private Long id;
    private Long projectId;
    private String name;
    private String color;
}