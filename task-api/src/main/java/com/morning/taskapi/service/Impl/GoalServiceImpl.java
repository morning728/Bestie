package com.morning.taskapi.service.Impl;

import com.morning.taskapi.model.Goal;
import com.morning.taskapi.repository.GoalRepository;
import com.morning.taskapi.service.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;


    @Override
    public Goal findByUsername(String username) {
        Goal goal = goalRepository.findByUsername(username);
        if(goal == null){
            log.info("No one goal was found from " + username);
            return null;
        }
        log.info(String.format("%s's goal was found"), username);
        return goal;
    }

    public Goal findById(Long id) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if(goal == null){
            log.info("No one goal was found with id = " + id);
            return null;
        }
        log.info(String.format("Goal with id = %s was found"), id);
        return goal;
    }

    @Override
    public Goal addGoal(Goal goal) {
        Goal result = null;
        try{
            result = goalRepository.save(goal);
        } catch (Exception e) {
            log.error("The goal was not added due to the error: " + e.toString());
            return null;
        }
        log.info(String.format("%s's goal was successfully added!", goal.getUsername()));
        return result;
    }

    @Override
    public Goal updateGoal(Goal goal) {
        Goal result = null;
        Goal toUpdate = goalRepository.findByUsername(goal.getUsername());
        toUpdate.setDescription(goal.getDescription());
        toUpdate.setWeight(goal.getWeight());
        toUpdate.setIncome(goal.getIncome());
        toUpdate.setSheetsPerDay(goal.getSheetsPerDay());
        toUpdate.setStepsPerDay(goal.getStepsPerDay());
        toUpdate.setWeightDifferencePerDay(goal.getWeightDifferencePerDay());
        toUpdate.setUpdated(new Date());
        try{
             result = goalRepository.save(toUpdate);
        } catch (Exception e) {
            log.error("The goal was not updated due to the error: " + e.toString());
            return null;
        }
        log.info(String.format("%s's goal was successfully updated!", goal.getUsername()));
        return result;
    }

    @Override
    public void deleteGoal(String username) {
        try{
            goalRepository.delete(goalRepository.findByUsername(username));
        } catch (Exception e) {
            log.error("The goal was not deleted due to the error: " + e.toString());
            return;
        }
        log.info(String.format("%s's goal was successfully deleted!", username));
    }

    public void deleteGoalById(Long id) {
        Goal toDelete = goalRepository.findById(id).orElse(null);
        if(toDelete == null){
            log.error(String.format("The goal with id = %s was not found", id));
            return;
        }
        try{
            goalRepository.delete(toDelete);
        } catch (Exception e) {
            log.error("The goal was not deleted due to the error: " + e.toString());
            return;
        }
        log.info(String.format("Goal with id = %s was successfully deleted!", id));
    }
}