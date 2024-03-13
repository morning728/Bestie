package com.morning.taskapi.controller.controllerAdvice;

import com.morning.taskapi.exception.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RecordNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity exceptionHandler(RecordNotFoundException exception){

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", exception.getMessage());

        return new ResponseEntity(errorMap, HttpStatus.NOT_FOUND);
    }
}
