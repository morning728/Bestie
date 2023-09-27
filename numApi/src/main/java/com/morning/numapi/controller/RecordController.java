package com.morning.numapi.controller;


import com.morning.numapi.exception.RecordNotFoundException;
import com.morning.numapi.model.DTO.RecordDTO;
import com.morning.numapi.model.Record;
import com.morning.numapi.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
//@CrossOrigin("http://localhost:3000")
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/sorted")
    public List<Record> getSortedRecords(@RequestParam(name = "username", required = true) String username){
        return recordService.findByUsernameSortedByDate(username);
    }
    @GetMapping("/sorted-reversed")
    public List<Record> getReversedSortedRecords(@RequestParam(name = "username", required = true) String username){
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
    public ResponseEntity getRecords(@RequestParam(name = "username", required = false) String username) {
        if(username == null) return new ResponseEntity<>(recordService.findAll(), HttpStatus.OK);
        return new ResponseEntity<>(recordService.findByUsername(username), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity addNewRecord(@RequestBody RecordDTO dto){
        try {
            recordService.addRecord(dto.toRecord());
        } catch (Exception e){
            return (ResponseEntity) ResponseEntity.of(ProblemDetail.forStatus(404)); // FIX
        }
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity updateRecord(@PathVariable(name = "id") Long id, @RequestBody RecordDTO dto) {////////////////////////
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

}
