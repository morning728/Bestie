package com.morning.openrequesthandler.errorhandling;


import com.morning.openrequesthandler.exception.BadRequestException;
import com.morning.openrequesthandler.exception.ExceptionResponse;
import com.morning.openrequesthandler.exception.annotation.BadRequestExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = BadRequestExceptionHandler.class)
public class BadRequestAdvice {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadRequestException e) {
        ExceptionResponse response = new ExceptionResponse("ERROR: ".concat(e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
