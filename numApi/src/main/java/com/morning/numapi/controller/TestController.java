package com.morning.numapi.controller;

import com.morning.numapi.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {
    private final StatisticsService statisticsService;

    @GetMapping("")
    public ResponseEntity test() throws ParseException {
        log.info(statisticsService.getStatisticsPerPeriod(new Date(2023, 9, 10), new Date(2023, 9, 12), "user").toString());
        return (ResponseEntity) ResponseEntity.ok(200);
    }
}
