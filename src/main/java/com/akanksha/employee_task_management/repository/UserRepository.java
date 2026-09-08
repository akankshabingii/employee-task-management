package com.akanksha.employee_task_management.repository;

import com.akanksha.employee_task_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByManagerId(Integer managerId);

    List<User> findByTeamId(Integer teamId);
}