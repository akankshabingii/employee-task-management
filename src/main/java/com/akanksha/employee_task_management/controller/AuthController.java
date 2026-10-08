package com.akanksha.employee_task_management.controller;

import com.akanksha.employee_task_management.dto.LoginRequest;
import com.akanksha.employee_task_management.dto.LoginResponse;
import com.akanksha.employee_task_management.dto.ManagerOption;
import com.akanksha.employee_task_management.dto.SignupRequest;
import com.akanksha.employee_task_management.dto.TeamResponse;
import com.akanksha.employee_task_management.dto.UserResponse;
import com.akanksha.employee_task_management.entity.User;
import com.akanksha.employee_task_management.service.TeamService;
import com.akanksha.employee_task_management.service.UserService;
import com.akanksha.employee_task_management.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TeamService teamService;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            TeamService teamService) {

        this.userService = userService;
        this.teamService = teamService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.login(request);

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                userResponse
        );
    }

    // =========================
    // SIGNUP (public)
    // =========================

    @PostMapping("/signup")
    public LoginResponse signup(
            @Valid @RequestBody SignupRequest request) {

        User user = userService.signup(request);

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }

    // Teams for the signup form
    @GetMapping("/teams")
    public List<TeamResponse> signupTeams() {

        return teamService.getAllTeams()
                .stream()
                .map(t -> new TeamResponse(t.getId(), t.getName()))
                .toList();
    }

    // Managers for the signup form (id, name, team only)
    @GetMapping("/managers")
    public List<ManagerOption> signupManagers() {

        return userService.getAllUsers()
                .stream()
                .filter(u -> "MANAGER".equals(u.getRole()))
                .map(u -> new ManagerOption(
                        u.getId(),
                        u.getName(),
                        u.getTeam() != null ? u.getTeam().getId() : null
                ))
                .toList();
    }
}