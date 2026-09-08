package com.akanksha.employee_task_management.service;

import com.akanksha.employee_task_management.dto.TeamRequest;
import com.akanksha.employee_task_management.entity.Team;
import com.akanksha.employee_task_management.exception.ResourceNotFoundException;
import com.akanksha.employee_task_management.repository.TeamRepository;
import com.akanksha.employee_task_management.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository) {

        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Integer id) {
        return teamRepository.findById(id);
    }

    public Team saveTeam(Team team) {

        if (teamRepository.existsByName(team.getName())) {
            throw new RuntimeException(
                    "Team already exists"
            );
        }

        return teamRepository.save(team);
    }

    public Team updateTeam(
            Integer id,
            TeamRequest request) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found with id: " + id
                        )
                );

        if (!team.getName().equals(request.getName())
                && teamRepository.existsByName(request.getName())) {

            throw new RuntimeException(
                    "Team already exists"
            );
        }

        team.setName(request.getName());

        return teamRepository.save(team);
    }

    public void deleteTeam(Integer id) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found with id: " + id
                        )
                );

        if (!userRepository.findByTeamId(id).isEmpty()) {

            throw new RuntimeException(
                    "Cannot delete team while users are assigned to it"
            );
        }

        teamRepository.delete(team);
    }
}