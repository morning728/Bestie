package com.morning.taskapimain.errorhandling;

import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.ExceptionResponse;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = AccessExceptionHandler.class)
public class AccessAdvice {
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessException e) {
        ExceptionResponse response = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
