package com.akanksha.employee_task_management.controller;

import com.akanksha.employee_task_management.dto.LoginRequest;
import com.akanksha.employee_task_management.dto.LoginResponse;
import com.akanksha.employee_task_management.dto.UserResponse;
import com.akanksha.employee_task_management.entity.User;
import com.akanksha.employee_task_management.service.UserService;
import com.akanksha.employee_task_management.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
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
}