package com.morning.statisticsapi.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(Long id){
        super("Could not found the record with id " + id);
    }
}
