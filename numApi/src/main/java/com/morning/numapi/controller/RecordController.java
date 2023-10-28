package com.morning.numapi.controller;


import com.morning.numapi.exception.RecordNotFoundException;
import com.morning.numapi.model.DTO.RecordDTO;
import com.morning.numapi.model.Record;
import com.morning.numapi.service.JwtService;
import com.morning.numapi.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final JwtService jwtService;

    @GetMapping("/sorted")
    public List<Record> getSortedRecords(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        String username = jwtService.extractUsername(
                token.substring(7)
        );
        return recordService.findByUsernameSortedByDate(username);
    }
    @GetMapping("/sorted-reversed")
    public List<Record> getReversedSortedRecords(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        String username = jwtService.extractUsername(
                token.substring(7)
        );
        return recordService.findByUsernameSortedByDateReversed(username);
    }

    @GetMapping("/{id}")
    public ResponseEntity getRecordById(@PathVariable(name = "id") Long id) {
        if (recordService.existsById(id)) {
            return new ResponseEntity<>(recordService.findById(id), HttpStatus.OK);
        } else {
            throw new RecordNotFoundException(id);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Record>> getRecords(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                   @RequestParam(required = false, value = "findRecord") String findRecordParam) {
        String username = jwtService.extractUsername(
                token.substring(7)
        );
        if(findRecordParam != null){
            return new ResponseEntity(recordService.findAllByUsernameWithParam(username, findRecordParam), HttpStatus.OK);
        }
        return new ResponseEntity(recordService.findAllByUsername(username), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity addNewRecord(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RecordDTO dto
    ){
        dto.setUsername(jwtService.extractUsername(token.substring(7)));
        try {
            recordService.addRecord(dto.toRecord());
        } catch (Exception e){
            return (ResponseEntity) ResponseEntity.of(ProblemDetail.forStatus(404)); // FIX
        }
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity updateRecord(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                       @PathVariable(name = "id") Long id,
                                       @RequestBody RecordDTO dto
    ) {
        dto.setUsername(jwtService.extractUsername(token.substring(7)));
        if(recordService.existsById(id)){
            return ResponseEntity.ok(recordService.updateRecord(dto.toRecord(id)));
        }
        throw new RecordNotFoundException(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRecord(@PathVariable(name = "id") Long id) {////////////////////////
        if(recordService.existsById(id)){
            recordService.deleteRecord(id);
            return ResponseEntity.ok(String.format("Record with id %s has been deleted successfully!", id));
        }
        throw new RecordNotFoundException(id);
    }

    @GetMapping("/last")
    public ResponseEntity getLastFromUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        String username = jwtService.extractUsername(
                token.substring(7)
        );
        return new ResponseEntity<>(recordService.findLastRecordFromUser(username), HttpStatusCode.valueOf(200));
    }

}
