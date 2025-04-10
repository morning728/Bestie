package com.morning.security.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProfileInfoDTO {
    private String username;
    private String email;
    private String telegramId;
    private String chatId;
}
