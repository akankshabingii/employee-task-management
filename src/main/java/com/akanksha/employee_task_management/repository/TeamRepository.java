package com.akanksha.employee_task_management.repository;

import com.akanksha.employee_task_management.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    boolean existsByName(String name);
}