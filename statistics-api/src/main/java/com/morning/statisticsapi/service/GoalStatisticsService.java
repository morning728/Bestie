package com.morning.statisticsapi.service;

import com.morning.statisticsapi.exception.RecordNotFoundException;
import com.morning.statisticsapi.model.BaseEntity;
import com.morning.statisticsapi.model.Goal;
import com.morning.statisticsapi.model.GoalStatiscticsModel.GeneralStat;
import com.morning.statisticsapi.model.Record;
import com.morning.statisticsapi.service.interf.GoalService;
import com.morning.statisticsapi.service.interf.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalStatisticsService {
    private final GoalService goalService;
    private final RecordService recordService;

    /*
    В этом методе учитываются только записи юзера за последний месяц, чтобы была видна актуальная информация
    и прогресс.
     */
    public GeneralStat getGeneralStatistics(String username){
        Date currentDate = Calendar.getInstance().getTime();
        Calendar calendarMonthAgo = Calendar.getInstance();
        calendarMonthAgo.add(Calendar.MONTH, -1);
        Date dateMonthAgo = calendarMonthAgo.getTime();
        List<Record> recordsForLastMonth = recordService.findByCreatedBetweenAndUsername(
                currentDate,
                dateMonthAgo,
                username);
        if (recordsForLastMonth.isEmpty()){
            throw new RecordNotFoundException("No one record was found for the last month");
        }
        Goal goal = goalService.findByUsername(username);
        if (goal == null){
            throw new RecordNotFoundException("No one goal was found!");  //TEMP
        }

        recordsForLastMonth.sort((Comparator.comparing(BaseEntity::getCreated)));

        Float weightDifferencePerDay = (recordsForLastMonth.get(recordsForLastMonth.size() - 1).getWeight() -
                                        recordsForLastMonth.get(0).getWeight()) / 30;
        Double sheetsPerDay = recordService.extractAverage(username, recordsForLastMonth, Record::getSheets);
        Double stepsPerDay = recordService.extractAverage(username, recordsForLastMonth, Record::getSteps);



        HashMap<String, Pair<Float, Date>> extraResults = new HashMap<>();
        extraResults.put(
                "bestSheetsDay",
                Pair.of(
                    Float.valueOf(recordsForLastMonth.stream().max(Comparator.comparing(Record::getSheets)).get().getSheets()),
                    recordsForLastMonth.stream().max(Comparator.comparing(Record::getSheets)).get().getCreated()
                ));
        extraResults.put(
                "worstSheetsDay",
                Pair.of(
                        Float.valueOf(recordsForLastMonth.stream().min(Comparator.comparing(Record::getSheets)).get().getSheets()),
                        recordsForLastMonth.stream().min(Comparator.comparing(Record::getSheets)).get().getCreated()
                ));
        extraResults.put(
                "bestStepsDay",
                Pair.of(
                        Float.valueOf(recordsForLastMonth.stream().max(Comparator.comparing(Record::getSteps)).get().getSteps()),
                        recordsForLastMonth.stream().max(Comparator.comparing(Record::getSteps)).get().getCreated()
                ));
        extraResults.put(
                "worstStepsDay",
                Pair.of(
                        Float.valueOf(recordsForLastMonth.stream().min(Comparator.comparing(Record::getSteps)).get().getSteps()),
                        recordsForLastMonth.stream().min(Comparator.comparing(Record::getSteps)).get().getCreated()
                ));

        return GeneralStat.builder()
                .weight(Pair.of(recordService.findLastRecordFromUser(username).getWeight(),
                        goal.getWeight()))
                .weightDifferencePerDay(Pair.of(weightDifferencePerDay,
                        goal.getWeightDifferencePerDay()))
                .sheetsPerDay(Pair.of(sheetsPerDay.floatValue(), Float.valueOf(goal.getSheetsPerDay())))
                .stepsPerDay(Pair.of(stepsPerDay.floatValue(), Float.valueOf(goal.getStepsPerDay())))
                .extraResults(extraResults)
                .build();
    }
}
