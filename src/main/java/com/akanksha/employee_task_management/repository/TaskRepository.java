package com.akanksha.employee_task_management.repository;

import com.akanksha.employee_task_management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    boolean existsByTitleAndEmployeeId(
            String title,
            Integer employeeId
    );

    List<Task> findByEmployeeId(Integer employeeId);
    List<Task> findByManagerId(Integer managerId);
}