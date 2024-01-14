package com.morning.statisticsapi.model.GoalStatiscticsModel;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.HashMap;


@Data
@Builder
public class GeneralStat {
    private Pair<Float, Float> weight;
    private Pair<Float, Float> weightDifferencePerDay;

    //private Pair<Float, Float> income;

    private Pair<Float, Float> sheetsPerDay;
    private Pair<Float, Float> stepsPerDay;

    private HashMap<String, Pair<Float, Date>> extraResults;

}
