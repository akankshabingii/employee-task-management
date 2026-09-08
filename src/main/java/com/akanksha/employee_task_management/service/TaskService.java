package com.akanksha.employee_task_management.service;

import com.akanksha.employee_task_management.dto.TaskRequest;
import com.akanksha.employee_task_management.entity.Task;
import com.akanksha.employee_task_management.entity.User;
import com.akanksha.employee_task_management.exception.ResourceNotFoundException;
import com.akanksha.employee_task_management.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // =========================
    // GET ALL TASKS
    // =========================

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // =========================
    // GET TASK BY ID
    // =========================

    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    // =========================
    // GET TASKS FOR CURRENT USER
    // =========================

    public List<Task> getTasksForUser(User user) {

        String role = user.getRole();

        if ("ADMIN".equals(role)) {
            return taskRepository.findAll();
        }

        if ("MANAGER".equals(role)) {
            return taskRepository.findByManagerId(user.getId());
        }

        if ("EMPLOYEE".equals(role)) {
            return taskRepository.findByEmployeeId(user.getId());
        }

        return List.of();
    }

    // =========================
    // GET ONE TASK FOR CURRENT USER
    // =========================

    public Optional<Task> getTaskForUser(
            Integer taskId,
            User user) {

        Optional<Task> optionalTask =
                taskRepository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return Optional.empty();
        }

        Task task = optionalTask.get();

        String role = user.getRole();

        if ("ADMIN".equals(role)) {
            return Optional.of(task);
        }

        if ("MANAGER".equals(role)
                && task.getManager() != null
                && task.getManager().getId().equals(user.getId())) {

            return Optional.of(task);
        }

        if ("EMPLOYEE".equals(role)
                && task.getEmployee() != null
                && task.getEmployee().getId().equals(user.getId())) {

            return Optional.of(task);
        }

        return Optional.empty();
    }

    // =========================
    // CREATE TASK
    // =========================

    public Task saveTask(Task task) {

        validateTaskValues(
                task.getPriority(),
                task.getStatus(),
                task.getStartDate(),
                task.getEndDate()
        );

        if (taskRepository.existsByTitleAndEmployeeId(
                task.getTitle(),
                task.getEmployee().getId())) {

            throw new RuntimeException(
                    "Task already exists"
            );
        }

        return taskRepository.save(task);
    }

    // =========================
    // UPDATE TASK
    // =========================

    public Task updateTask(
            Integer id,
            TaskRequest request,
            User employee,
            User manager) {

        Task existingTask =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Task not found with id: " + id
                                )
                        );

        validateTaskValues(
                request.getPriority(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (!existingTask.getTitle().equals(request.getTitle())
                && taskRepository.existsByTitleAndEmployeeId(
                request.getTitle(),
                employee.getId())) {

            throw new RuntimeException(
                    "Task already exists"
            );
        }

        validateStatusTransition(
                existingTask.getStatus(),
                request.getStatus()
        );

        existingTask.setTitle(request.getTitle());
        existingTask.setPriority(request.getPriority());
        existingTask.setProgress(request.getProgress());
        existingTask.setStatus(request.getStatus());
        existingTask.setDescription(request.getDescription());
        existingTask.setStartDate(request.getStartDate());
        existingTask.setEndDate(request.getEndDate());
        existingTask.setEmployee(employee);
        existingTask.setManager(manager);

        return taskRepository.save(existingTask);
    }

    // =========================
    // VALIDATE TASK VALUES
    // =========================

    private void validateTaskValues(
            String priority,
            String status,
            LocalDate startDate,
            LocalDate endDate) {

        priority = normalize(priority);
        status = normalize(status);

        if (!"LOW".equals(priority)
                && !"MEDIUM".equals(priority)
                && !"HIGH".equals(priority)) {

            throw new IllegalArgumentException(
                    "Priority must be LOW, MEDIUM, or HIGH"
            );
        }

        if (!"PENDING".equals(status)
                && !"IN_PROGRESS".equals(status)
                && !"COMPLETED".equals(status)) {

            throw new IllegalArgumentException(
                    "Status must be PENDING, IN_PROGRESS, or COMPLETED"
            );
        }

        if (startDate != null
                && endDate != null
                && endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    // =========================
    // STATUS TRANSITIONS
    // =========================

    private void validateStatusTransition(
            String oldStatus,
            String newStatus) {

        oldStatus = normalize(oldStatus);
        newStatus = normalize(newStatus);

        if (java.util.Objects.equals(oldStatus, newStatus)) {
            return;
        }

        if ("PENDING".equals(oldStatus)
                && "IN_PROGRESS".equals(newStatus)) {
            return;
        }

        if ("IN_PROGRESS".equals(oldStatus)
                && "COMPLETED".equals(newStatus)) {
            return;
        }

        throw new IllegalArgumentException(
                "Invalid status transition from "
                        + oldStatus
                        + " to "
                        + newStatus
        );
    }

    private String normalize(String value) {
        return value == null
                ? null
                : value.trim().toUpperCase();
    }

    // =========================
    // GET TASKS BY EMPLOYEE
    // =========================

    public List<Task> getTasksByEmployeeId(
            Integer employeeId) {

        return taskRepository.findByEmployeeId(employeeId);
    }

    // =========================
    // DELETE TASK
    // =========================

    public void deleteTask(Integer id) {

        if (!taskRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Task not found with id: " + id
            );
        }

        taskRepository.deleteById(id);
    }
}