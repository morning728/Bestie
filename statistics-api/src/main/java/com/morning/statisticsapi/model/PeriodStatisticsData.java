package com.morning.statisticsapi.model;

import lombok.Data;

@Data
public class PeriodStatisticsData {

    private String username;
    private Integer numberOfDays;

    private Float averageMark;
    private Float averageMoodMark;


    private Float totalWeightDifference;
    private Float totalHeightDifference;
    private Integer totalSteps;
    private Integer totalSheets;
    private Float totalIncome;
}
