package com.morning.taskapimain.entity.dto;


import com.morning.taskapimain.entity.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AboutMeDTO {
    private Long id;
    private String username;

    public static AboutMeDTO fromUser(User user) {
        return AboutMeDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
