package com.morning.statisticsapi.service.interf;


import com.morning.statisticsapi.model.Goal;

public interface GoalService {
    Goal findByUsername(String username);
    Goal findById(Long id);
    Goal addGoal(Goal goal);
    Goal updateGoal(Goal goal);
    void deleteGoal(String username);
    void deleteGoalById(Long id);
}
