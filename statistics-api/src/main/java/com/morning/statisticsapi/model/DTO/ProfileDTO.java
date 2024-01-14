package com.morning.statisticsapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDTO {


    private String username;
    private String firstName;
    private String lastName;
    private Date birthday;
    private Float weight;
    private Float height;
    private Double averageMark;
    private Double averageMoodMark;
    private Double averageStepsPerDay;
    private Double averageSheetsPerDay;
    private Double averageIncomePerDay;
    private Double averageSymbolsPerDescription;



}
