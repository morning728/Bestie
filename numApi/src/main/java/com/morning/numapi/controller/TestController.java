package com.morning.numapi.controller;

import com.morning.numapi.repository.RecordRepository;
import com.morning.numapi.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {
    private final StatisticsService statisticsService;
    private final RecordRepository recordRepository;

    @GetMapping("")
    public String test() throws ParseException {
        return recordRepository.findLastRecordFromUser("use").toString();
    }
}
