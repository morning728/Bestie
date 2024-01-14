package com.morning.statisticsapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morning.statisticsapi.model.Goal;
import com.morning.statisticsapi.model.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoalDTO {
    private Long id;
    private String username;
    private String description;
    private Float weight;
    private Float weightDifferencePerDay;
    private Integer stepsPerDay;
    private Integer sheetsPerDay;
    private Integer income;

    public Goal toGoal(){
        Goal result = new Goal();

        result.setId(id == null ? null : id);  //FIX
        result.setUsername(username);
        result.setDescription(description);
        result.setWeight(weight == null ? 0 : weight);
        result.setStepsPerDay(stepsPerDay == null ? 0 : stepsPerDay);
        result.setSheetsPerDay(sheetsPerDay == null ? 0 : sheetsPerDay);
        result.setIncome(income == null ? 0 : income);
        result.setWeightDifferencePerDay(weightDifferencePerDay == null ? 0 : weightDifferencePerDay);
        result.setCreated(new Date());
        result.setUpdated(new Date());
        result.setStatus(Status.ACTIVE);

        return result;
    }

    public GoalDTO fromGoal(Goal goal){
        GoalDTO dto = new GoalDTO();

        dto.setId(goal.getId());
        dto.setUsername(goal.getUsername());
        dto.setDescription(goal.getDescription());
        dto.setWeight(goal.getWeight());
        dto.setWeightDifferencePerDay(goal.getWeightDifferencePerDay());
        dto.setStepsPerDay(goal.getStepsPerDay());
        dto.setSheetsPerDay(goal.getSheetsPerDay());
        dto.setIncome(goal.getIncome());

        return dto;
    }
}
