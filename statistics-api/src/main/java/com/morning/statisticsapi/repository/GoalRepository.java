package com.morning.statisticsapi.repository;


import com.morning.statisticsapi.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Goal findByUsername(String name);
}
