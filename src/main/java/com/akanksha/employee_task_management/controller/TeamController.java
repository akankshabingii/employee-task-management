package com.akanksha.employee_task_management.controller;

import com.akanksha.employee_task_management.dto.TeamRequest;
import com.akanksha.employee_task_management.dto.TeamResponse;
import com.akanksha.employee_task_management.dto.UserResponse;
import com.akanksha.employee_task_management.entity.Team;
import com.akanksha.employee_task_management.exception.ResourceNotFoundException;
import com.akanksha.employee_task_management.service.TeamService;
import com.akanksha.employee_task_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    public TeamController(
            TeamService teamService,
            UserService userService) {

        this.teamService = teamService;
        this.userService = userService;
    }

    // =========================
    // GET ALL TEAMS
    // =========================
    @GetMapping
    public List<TeamResponse> getAllTeams() {

        return teamService.getAllTeams()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================
    // GET TEAM BY ID
    // =========================
    @GetMapping("/{id}")
    public TeamResponse getTeamById(@PathVariable Integer id) {

        Team team = teamService.getTeamById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found with id: " + id
                        )
                );

        return convertToResponse(team);
    }

    // =========================
    // GET USERS OF TEAM
    // =========================
    @GetMapping("/{id}/users")
    public List<UserResponse> getUsersByTeamId(
            @PathVariable Integer id) {

        // Check if team exists
        teamService.getTeamById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found with id: " + id
                        )
                );

        return userService.getUsersByTeamId(id)
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();
    }

    // =========================
    // CREATE TEAM
    // =========================
    @PostMapping
    public TeamResponse createTeam(
            @Valid @RequestBody TeamRequest request) {

        Team team = new Team();
        team.setName(request.getName());

        Team savedTeam = teamService.saveTeam(team);

        return convertToResponse(savedTeam);
    }

    // =========================
    // UPDATE TEAM
    // =========================
    @PutMapping("/{id}")
    public TeamResponse updateTeam(
            @PathVariable Integer id,
            @Valid @RequestBody TeamRequest request) {

        Team updatedTeam = teamService.updateTeam(id, request);

        return convertToResponse(updatedTeam);
    }

    // =========================
    // DELETE TEAM
    // =========================
    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Integer id) {

        teamService.deleteTeam(id);
    }

    // =========================
    // TEAM -> TEAM RESPONSE
    // =========================
    private TeamResponse convertToResponse(Team team) {

        return new TeamResponse(
                team.getId(),
                team.getName()
        );
    }
}