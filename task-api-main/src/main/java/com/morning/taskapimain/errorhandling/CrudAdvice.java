package com.morning.taskapimain.errorhandling;

import com.morning.taskapimain.exception.ExceptionResponse;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = CrudExceptionHandler.class)
public class CrudAdvice {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
