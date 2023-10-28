package com.morning.numapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morning.numapi.model.Record;
import com.morning.numapi.model.User;
import com.morning.numapi.service.RecordService;
import com.morning.numapi.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

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
