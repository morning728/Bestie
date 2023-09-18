package com.morning.numapi.controller;

import com.morning.numapi.repository.RecordRepository;
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
    private final RecordRepository recordRepository;

    @GetMapping("")
    public ResponseEntity test() throws ParseException {
        log.info(
                recordRepository.findByCreatedBetweenAndUsername(
                        new Date(1694299242000L),
                        new Date(1694644842000L),
                        "user").toString()
                );
        return (ResponseEntity) ResponseEntity.ok(statisticsService.getStatisticsPerPeriod(
                new Date(1694299242000L),
                new Date(1694644842000L),
                "user")
        );
    }
}
