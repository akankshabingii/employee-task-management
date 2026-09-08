package com.akanksha.employee_task_management.controller;

import com.akanksha.employee_task_management.dto.TaskResponse;
import com.akanksha.employee_task_management.dto.UserRequest;
import com.akanksha.employee_task_management.dto.UserResponse;
import com.akanksha.employee_task_management.entity.Task;
import com.akanksha.employee_task_management.entity.User;
import com.akanksha.employee_task_management.exception.ResourceNotFoundException;
import com.akanksha.employee_task_management.service.TaskService;
import com.akanksha.employee_task_management.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(
            UserService userService,
            TaskService taskService) {

        this.userService = userService;
        this.taskService = taskService;
    }

    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Integer id,
            Authentication authentication) {

        User currentUser =
                getAuthenticatedUser(authentication);

        User targetUser =
                userService.getUserById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        if (!userService.canAccessUser(
                currentUser,
                targetUser)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        return ResponseEntity.ok(
                convertToUserResponse(targetUser)
        );
    }

    // =========================
    // GET USER TASKS
    // =========================

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>>
    getTasksByUserId(
            @PathVariable Integer id,
            Authentication authentication) {

        User currentUser =
                getAuthenticatedUser(authentication);

        User targetUser =
                userService.getUserById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        if (!userService.canAccessUser(
                currentUser,
                targetUser)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        List<TaskResponse> response =
                taskService.getTasksByEmployeeId(id)
                        .stream()
                        .map(this::convertTaskToResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // =========================
    // CREATE USER
    // =========================

    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody UserRequest request) {

        User savedUser =
                userService.createUser(request);

        return convertToUserResponse(savedUser);
    }

    // =========================
    // UPDATE USER
    // =========================

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequest request) {

        User updatedUser =
                userService.updateUser(
                        id,
                        request
                );

        return convertToUserResponse(updatedUser);
    }

    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Integer id) {

        userService.deleteUser(id);
    }

    // =========================
    // GET EMPLOYEES OF MANAGER
    // =========================

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<UserResponse>>
    getEmployeesByManagerId(
            @PathVariable Integer id,
            Authentication authentication) {

        User currentUser =
                getAuthenticatedUser(authentication);

        User manager =
                userService.getUserById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        if (!"MANAGER".equals(manager.getRole())) {

            throw new IllegalArgumentException(
                    "User is not a manager"
            );
        }

        if (!userService.canAccessEmployees(
                currentUser,
                manager)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        List<UserResponse> response =
                userService
                        .getEmployeesByManagerId(id)
                        .stream()
                        .map(this::convertToUserResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET AUTHENTICATED USER
    // =========================

    private User getAuthenticatedUser(
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    // =========================
    // USER RESPONSE
    // =========================

    private UserResponse convertToUserResponse(
            User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    // =========================
    // TASK RESPONSE
    // =========================

    private TaskResponse convertTaskToResponse(
            Task task) {

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.getProgress(),
                task.getStatus(),
                task.getDescription(),
                task.getStartDate(),
                task.getEndDate(),

                task.getEmployee() != null
                        ? task.getEmployee().getId()
                        : null,

                task.getEmployee() != null
                        ? task.getEmployee().getName()
                        : null,

                task.getManager() != null
                        ? task.getManager().getId()
                        : null,

                task.getManager() != null
                        ? task.getManager().getName()
                        : null
        );
    }
}