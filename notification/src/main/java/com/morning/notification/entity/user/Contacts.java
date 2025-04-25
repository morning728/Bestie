package com.morning.notification.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {
    private String username;
    private String email;
    private String chatId;
    private String telegramId;
}
