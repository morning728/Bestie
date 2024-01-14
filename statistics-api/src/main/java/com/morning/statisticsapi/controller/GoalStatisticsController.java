package com.morning.statisticsapi.controller;

import com.morning.statisticsapi.service.GoalStatisticsService;
import com.morning.statisticsapi.service.interf.GoalService;
import com.morning.statisticsapi.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
@Slf4j
@RequestMapping("/statistics/goal")
@RequiredArgsConstructor
public class GoalStatisticsController {
    private final JwtService jwtService;
    private final GoalStatisticsService statisticsService;

    @GetMapping("")
    public ResponseEntity getGeneralGoalStatistics(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        return ResponseEntity.ok(
                statisticsService.getGeneralStatistics(jwtService.extractUsername(token.substring(7)))
        );
    }
}
