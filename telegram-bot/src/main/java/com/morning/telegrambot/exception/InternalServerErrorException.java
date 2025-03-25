package com.morning.telegrambot.exception;

// Ошибка 500+ - ошибка на сервере
public class InternalServerErrorException extends FeignClientException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
