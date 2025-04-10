package com.morning.telegrambot.exception;

// Ошибка 404 - ресурс не найден
public class ResourceNotFoundException extends FeignClientException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
