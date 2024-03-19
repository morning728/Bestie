package com.morning.taskapimain.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProfileDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String telegramId;
    private String password;
}
