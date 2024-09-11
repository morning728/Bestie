package com.morning.taskapimain.errorhandling;

import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.ExceptionResponse;
import com.morning.taskapimain.exception.KafkaException;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = BadRequestExceptionHandler.class)
public class BadRequestAdvice {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadRequestException e) {
        ExceptionResponse response = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ExceptionResponse> handleException(KafkaException e) {
        ExceptionResponse response = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
