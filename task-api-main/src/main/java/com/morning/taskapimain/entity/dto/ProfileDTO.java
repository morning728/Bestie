package com.morning.taskapimain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String telegramId;
    private String password;
    private String status;
    private LocalDateTime createdAt;

    public ProfileDTO(Long id, String username, String firstName, String lastName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;

    }

    public ProfileDTO getProfileInfoFromSecurityResponse(String response){

        this.email = response.contains("\"email\":\"") ?
                response.substring(response.indexOf("\"email\":\"") + 9, response.indexOf("\",", response.indexOf("\"email\":\"") + 9)) :
                null;
        this.telegramId = response.contains("\"telegramId\":\"") ?
                response.substring(response.indexOf("\"telegramId\":\"") + 14, response.indexOf("\"", response.indexOf("\"telegramId\":\"") + 14)) :
                null;
        return this;
    }
}
