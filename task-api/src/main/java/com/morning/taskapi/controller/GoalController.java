package com.morning.taskapi.controller;

import com.morning.taskapi.model.DTO.GoalDTO;
import com.morning.taskapi.service.GoalService;
import com.morning.taskapi.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final JwtService jwtService;

    @GetMapping("")
    public ResponseEntity getAllGoals(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){

        return new ResponseEntity<>(
                goalService.findByUsername(jwtService.extractUsername(token.substring(7))),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity getGoalById(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(goalService.findById(id), HttpStatus.OK);
    }



    @PostMapping("")
    public ResponseEntity addNewGoal(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                     @RequestBody GoalDTO dto){
        dto.setUsername(jwtService.extractUsername(token.substring(7)));
        try {
            goalService.addGoal(dto.toGoal());
        } catch (Exception e){
            return (ResponseEntity) ResponseEntity.of(ProblemDetail.forStatus(404)); // FIX
        }
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity updateGoal(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                     @PathVariable(name = "id") Long id,
                                     @RequestBody GoalDTO dto) {
        dto.setUsername(jwtService.extractUsername(token.substring(7)));
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
