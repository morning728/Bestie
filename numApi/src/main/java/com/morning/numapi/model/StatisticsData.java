package com.morning.numapi.model;

import lombok.Data;

@Data
public class StatisticsData {

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
