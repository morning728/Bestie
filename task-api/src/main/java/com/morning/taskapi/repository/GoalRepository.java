package com.morning.taskapi.repository;

import com.morning.taskapi.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Goal findByUsername(String name);
}
