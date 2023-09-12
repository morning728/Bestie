package com.morning.numapi.service;


import com.morning.numapi.model.Goal;

public interface GoalService {
    Goal findByUsername(String username);
    Goal findById(Long id);
    Goal addGoal(Goal goal);
    Goal updateGoal(Goal goal);
    void deleteGoal(String username);
    void deleteGoalById(Long id);
}
