package com.morning.numapi.controller;

import com.morning.numapi.model.DTO.GoalDTO;
import com.morning.numapi.model.DTO.RecordDTO;
import com.morning.numapi.model.Record;
import com.morning.numapi.service.GoalService;
import com.morning.numapi.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping("")
    public ResponseEntity getAllGoals(@RequestParam(name = "username") String username){
        return new ResponseEntity<>(goalService.findByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getGoalById(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(goalService.findById(id), HttpStatus.OK);
    }



    @PostMapping("")
    public ResponseEntity addNewGoal(@RequestBody GoalDTO dto){
        try {
            goalService.addGoal(dto.toGoal());
        } catch (Exception e){
            return (ResponseEntity) ResponseEntity.of(ProblemDetail.forStatus(404)); // FIX
        }
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity updateGoal(@PathVariable(name = "id") Long id, @RequestBody GoalDTO dto) {////////////////////////
        try {
            goalService.updateGoal(dto.toGoal());
        } catch (Exception e){
            return (ResponseEntity) ResponseEntity.of(ProblemDetail.forStatus(404)); // FIX
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGoal(@PathVariable(name = "id") Long id) {////////////////////////
        try {
            goalService.deleteGoalById(id);
        } catch (Exception e){
            return (ResponseEntity) ResponseEntity.of(ProblemDetail.forStatus(404)); // FIX
        }
        return ResponseEntity.ok(200);
    }
}
