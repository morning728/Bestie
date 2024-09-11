package com.morning.taskapimain.exception;

public class KafkaException extends Exception{
    public KafkaException(String message) {
        super(message);
    }
}