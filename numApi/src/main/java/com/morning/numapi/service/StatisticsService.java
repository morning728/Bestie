package com.morning.numapi.service;

import com.morning.numapi.model.Goal;
import com.morning.numapi.model.Record;
import com.morning.numapi.model.StatisticsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {
    private final GoalService goalService;
    private final RecordService recordService;

    public StatisticsData getStatisticsPerPeriod(
            Date firstDate,
            Date secondDate,
            String username){
        //Goal goal = goalService.findByUsername(username);
        List<Record> records = recordService.findByCreatedBetweenAndUsername(new Date(1694300667000L), new Date(1694646267000L), "user");
        log.info(records.toString());
        return null;
    }
}
