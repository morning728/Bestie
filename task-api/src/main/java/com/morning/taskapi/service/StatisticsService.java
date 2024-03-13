package com.morning.taskapi.service;

import com.morning.taskapi.model.Record;
import com.morning.taskapi.model.StatisticsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        List<Record> records = recordService.findByCreatedBetweenAndUsername(firstDate, secondDate, username);


        StatisticsData data = new StatisticsData();
        data.setUsername(username);
        data.setNumberOfDays(getNumberOfDaysBetween(firstDate, secondDate));

        if (records == null){
            return data;
        }
        records.sort(((o1, o2) -> o1.getCreated().compareTo(o2.getCreated())));
        data.setAverageMark(
                (float) records
                        .stream()
                        .mapToDouble(record -> Double.valueOf(record.getMark()))
                        .average().orElse(-1)
        );
        data.setAverageMoodMark(
                (float) records
                        .stream()
                        .mapToDouble(record -> Double.valueOf(record.getMoodMark()))
                        .average().orElse(-1)
        );
        data.setTotalWeightDifference(records.get(records.size() - 1).getWeight() - records.get(0).getWeight());
        data.setTotalHeightDifference(records.get(records.size() - 1).getHeight() - records.get(0).getHeight());
        data.setTotalSteps(records.stream().mapToInt(record -> record.getSteps()).sum());
        data.setTotalSheets(records.stream().mapToInt(record -> record.getSheets()).sum());
        data.setTotalIncome((float) records.stream().mapToDouble(record -> Double.valueOf(record.getIncome())).sum());
        return data;
    }

    private Integer getNumberOfDaysBetween(Date firstDate, Date secondDate){
        Long milliseconds = Math.abs(firstDate.getTime() - secondDate.getTime());
        return Math.toIntExact(((((milliseconds / 1000) / 60) / 60) / 24));
    }
}
