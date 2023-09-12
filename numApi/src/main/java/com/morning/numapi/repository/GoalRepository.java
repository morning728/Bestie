package com.morning.numapi.repository;

import com.morning.numapi.model.Goal;
import com.morning.numapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Goal findByUsername(String name);
}
