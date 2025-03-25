package com.morning.telegrambot.exception;
// Базовое исключение
public class FeignClientException extends RuntimeException {
    public FeignClientException(String message) {
        super(message);
    }
}