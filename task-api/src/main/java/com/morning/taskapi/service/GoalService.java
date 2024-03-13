package com.morning.taskapi.service;


import com.morning.taskapi.model.Goal;

public interface GoalService {
    Goal findByUsername(String username);
    Goal findById(Long id);
    Goal addGoal(Goal goal);
    Goal updateGoal(Goal goal);
    void deleteGoal(String username);
    void deleteGoalById(Long id);
}
