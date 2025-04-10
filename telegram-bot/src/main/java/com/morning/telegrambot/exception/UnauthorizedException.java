package com.morning.telegrambot.exception;

// Ошибка 401 - неавторизован
public class UnauthorizedException extends FeignClientException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
