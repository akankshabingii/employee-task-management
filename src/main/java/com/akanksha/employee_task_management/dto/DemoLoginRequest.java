
        package com.akanksha.employee_task_management.dto;

import jakarta.validation.constraints.NotBlank;

public class DemoLoginRequest {

    @NotBlank(message = "Role is required")
    private String role;

    public DemoLoginRequest() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
