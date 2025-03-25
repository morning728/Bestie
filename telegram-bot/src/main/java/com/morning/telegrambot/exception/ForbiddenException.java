package com.morning.telegrambot.exception;

// Ошибка 403 - доступ запрещен
public class ForbiddenException extends FeignClientException {
    public ForbiddenException(String message) {
        super(message);
    }
}
