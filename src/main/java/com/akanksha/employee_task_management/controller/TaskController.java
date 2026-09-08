package com.akanksha.employee_task_management.controller;

import com.akanksha.employee_task_management.dto.TaskRequest;
import com.akanksha.employee_task_management.dto.TaskResponse;
import com.akanksha.employee_task_management.entity.Task;
import com.akanksha.employee_task_management.entity.User;
import com.akanksha.employee_task_management.exception.ForbiddenException;
import com.akanksha.employee_task_management.exception.ResourceNotFoundException;
import com.akanksha.employee_task_management.service.TaskService;
import com.akanksha.employee_task_management.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(
            TaskService taskService,
            UserService userService) {

        this.taskService = taskService;
        this.userService = userService;
    }

    // =========================
    // GET TASKS
    // =========================

    @GetMapping
    public List<TaskResponse> getAllTasks() {

        User currentUser = getCurrentUser();

        return taskService
                .getTasksForUser(currentUser)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================
    // GET ONE TASK
    // =========================

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Integer id) {

        User currentUser = getCurrentUser();

        Task task = taskService
                .getTaskForUser(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found or access denied"
                        )
                );

        return convertToResponse(task);
    }

    // =========================
    // CREATE TASK
    // =========================

    @PostMapping
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request) {

        User currentUser = getCurrentUser();

        String role = currentUser.getRole();

        if ("EMPLOYEE".equals(role)) {

            throw new ForbiddenException(
                    "Employees cannot create tasks"
            );
        }

        User employee = userService
                .getUserById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: "
                                        + request.getEmployeeId()
                        )
                );

        User manager = userService
                .getUserById(request.getManagerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manager not found with id: "
                                        + request.getManagerId()
                        )
                );

        if (!"EMPLOYEE".equals(employee.getRole())) {

            throw new IllegalArgumentException(
                    "Task can only be assigned to an employee"
            );
        }

        if (!"MANAGER".equals(manager.getRole())) {

            throw new IllegalArgumentException(
                    "Selected user is not a manager"
            );
        }

        // =========================
        // MANAGER RULES
        // =========================

        if ("MANAGER".equals(role)) {

            if (!employeeBelongsToManager(
                    employee,
                    currentUser)) {

                throw new ForbiddenException(
                        "You can only assign tasks to your employees"
                );
            }

            if (!manager.getId()
                    .equals(currentUser.getId())) {

                throw new ForbiddenException(
                        "You can only create tasks under yourself"
                );
            }
        }

        // =========================
        // ADMIN
        // =========================

        if (!"ADMIN".equals(role)
                && !"MANAGER".equals(role)) {

            throw new ForbiddenException(
                    "You are not allowed to create tasks"
            );
        }

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setPriority(
                request.getPriority().trim().toUpperCase()
        );
        task.setProgress(request.getProgress());
        task.setStatus(
                request.getStatus().trim().toUpperCase()
        );
        task.setDescription(request.getDescription());
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        task.setEmployee(employee);
        task.setManager(manager);

        Task savedTask =
                taskService.saveTask(task);

        return convertToResponse(savedTask);
    }

    // =========================
    // UPDATE TASK
    // =========================

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Integer id,
            @Valid @RequestBody TaskRequest request) {

        User currentUser = getCurrentUser();

        Task existingTask = taskService
                .getTaskForUser(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found or access denied"
                        )
                );

        String role = currentUser.getRole();

        User employee = userService
                .getUserById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: "
                                        + request.getEmployeeId()
                        )
                );

        User manager = userService
                .getUserById(request.getManagerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manager not found with id: "
                                        + request.getManagerId()
                        )
                );

        if (!"EMPLOYEE".equals(employee.getRole())) {

            throw new IllegalArgumentException(
                    "Task can only be assigned to an employee"
            );
        }

        if (!"MANAGER".equals(manager.getRole())) {

            throw new IllegalArgumentException(
                    "Selected user is not a manager"
            );
        }

        // =========================
        // EMPLOYEE
        // =========================

        if ("EMPLOYEE".equals(role)) {

            if (existingTask.getEmployee() == null
                    || !existingTask.getEmployee()
                    .getId()
                    .equals(currentUser.getId())) {

                throw new ForbiddenException(
                        "You can only update your own tasks"
                );
            }

            // Employee cannot change assignment
            if (!employee.getId()
                    .equals(existingTask.getEmployee().getId())) {

                throw new ForbiddenException(
                        "Employees cannot reassign tasks"
                );
            }

            // Employee cannot change manager
            if (existingTask.getManager() == null
                    || !manager.getId()
                    .equals(existingTask.getManager().getId())) {

                throw new ForbiddenException(
                        "Employees cannot change the task manager"
                );
            }

            // Employee cannot modify task details
            if (!existingTask.getTitle()
                    .equals(request.getTitle())) {

                throw new ForbiddenException(
                        "Employees cannot change the task title"
                );
            }

            if (!existingTask.getPriority()
                    .equalsIgnoreCase(request.getPriority())) {

                throw new ForbiddenException(
                        "Employees cannot change task priority"
                );
            }

            if (!java.util.Objects.equals(
                    existingTask.getDescription(),
                    request.getDescription())) {

                throw new ForbiddenException(
                        "Employees cannot change task description"
                );
            }

            if (!java.util.Objects.equals(
                    existingTask.getStartDate(),
                    request.getStartDate())) {

                throw new ForbiddenException(
                        "Employees cannot change task start date"
                );
            }

            if (!java.util.Objects.equals(
                    existingTask.getEndDate(),
                    request.getEndDate())) {

                throw new ForbiddenException(
                        "Employees cannot change task end date"
                );
            }
        }

        // =========================
        // MANAGER
        // =========================

        if ("MANAGER".equals(role)) {

            if (existingTask.getManager() == null
                    || !existingTask.getManager()
                    .getId()
                    .equals(currentUser.getId())) {

                throw new ForbiddenException(
                        "You can only update your team's tasks"
                );
            }

            if (!employeeBelongsToManager(
                    employee,
                    currentUser)) {

                throw new ForbiddenException(
                        "You can only assign tasks to your employees"
                );
            }

            if (!manager.getId()
                    .equals(currentUser.getId())) {

                throw new ForbiddenException(
                        "You cannot change the task manager"
                );
            }
        }

        // =========================
        // ONLY ADMIN/MANAGER/EMPLOYEE
        // =========================

        if (!"ADMIN".equals(role)
                && !"MANAGER".equals(role)
                && !"EMPLOYEE".equals(role)) {

            throw new ForbiddenException(
                    "You are not allowed to update tasks"
            );
        }

        Task updatedTask =
                taskService.updateTask(
                        id,
                        request,
                        employee,
                        manager
                );

        return convertToResponse(updatedTask);
    }

    // =========================
    // DELETE TASK
    // =========================

    @DeleteMapping("/{id}")
    public void deleteTask(
            @PathVariable Integer id) {

        User currentUser = getCurrentUser();

        if (!"ADMIN".equals(currentUser.getRole())) {

            throw new ForbiddenException(
                    "Only admins can delete tasks"
            );
        }

        taskService.deleteTask(id);
    }

    // =========================
    // EMPLOYEE-MANAGER CHECK
    // =========================

    private boolean employeeBelongsToManager(
            User employee,
            User manager) {

        return employee.getManager() != null
                && employee.getManager()
                .getId()
                .equals(manager.getId());
    }

    // =========================
    // CURRENT USER
    // =========================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Current user not found"
                        )
                );
    }

    // =========================
    // RESPONSE
    // =========================

    private TaskResponse convertToResponse(Task task) {

        Integer employeeId = null;
        String employeeName = null;

        if (task.getEmployee() != null) {
            employeeId = task.getEmployee().getId();
            employeeName = task.getEmployee().getName();
        }

        Integer managerId = null;
        String managerName = null;

        if (task.getManager() != null) {
            managerId = task.getManager().getId();
            managerName = task.getManager().getName();
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String description =
                task.getDescription();

        if (authentication != null
                && authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        "ROLE_MANAGER"
                                .equals(a.getAuthority()))) {

            description = null;
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.getProgress(),
                task.getStatus(),
                description,
                task.getStartDate(),
                task.getEndDate(),
                employeeId,
                employeeName,
                managerId,
                managerName
        );
    }
}