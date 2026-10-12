package com.akanksha.employee_task_management.service;

import com.akanksha.employee_task_management.dto.LoginRequest;
import com.akanksha.employee_task_management.dto.SignupRequest;
import com.akanksha.employee_task_management.dto.UserRequest;
import com.akanksha.employee_task_management.entity.Team;
import com.akanksha.employee_task_management.entity.User;
import com.akanksha.employee_task_management.exception.ForbiddenException;
import com.akanksha.employee_task_management.exception.ResourceNotFoundException;
import com.akanksha.employee_task_management.exception.UnauthorizedException;
import com.akanksha.employee_task_management.repository.TeamRepository;
import com.akanksha.employee_task_management.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-signup-code:}")
    private String adminSignupCode;

    public UserService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // GET ALL USERS
    // =========================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // =========================
    // GET USER BY ID
    // =========================

    public Optional<User> getUserById(Integer id) {

        return userRepository.findById(id);
    }

    // =========================
    // GET USER BY EMAIL
    // =========================

    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    // =========================
    // GET USERS BY TEAM
    // =========================

    public List<User> getUsersByTeamId(Integer teamId) {

        return userRepository.findByTeamId(teamId);
    }

    // =========================
    // GET EMPLOYEES BY MANAGER
    // =========================

    public List<User> getEmployeesByManagerId(Integer managerId) {

        return userRepository.findByManagerId(managerId);
    }

    // =========================
    // LOGIN
    // =========================

    public User login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    // =========================
    // PUBLIC SIGNUP
    // =========================

    public User signup(SignupRequest request) {

        String role = request.getRole().trim().toUpperCase();

        if (role.equals("ADMIN")) {

            // If ADMIN_SIGNUP_CODE is set on the server, the code must match.
            // If it is not set, anyone can sign up as admin (open demo mode).
            if (adminSignupCode != null
                    && !adminSignupCode.isBlank()
                    && !adminSignupCode.equals(request.getAdminCode())) {

                throw new ForbiddenException(
                        "Invalid admin signup code"
                );
            }
        }

        UserRequest userRequest = new UserRequest();

        userRequest.setName(request.getName());
        userRequest.setEmail(request.getEmail());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(role);

        // Admins don't belong to a team or manager
        if (!role.equals("ADMIN")) {
            userRequest.setTeamId(request.getTeamId());
        }

        if (role.equals("EMPLOYEE")) {
            userRequest.setManagerId(request.getManagerId());
        }

        return createUser(userRequest);
    }

    // =========================
    // CREATE USER
    // =========================

    public User createUser(UserRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        // Validate role
        String role =
                request.getRole()
                        .trim()
                        .toUpperCase();

        if (!role.equals("ADMIN")
                && !role.equals("MANAGER")
                && !role.equals("EMPLOYEE")) {

            throw new IllegalArgumentException(
                    "Role must be ADMIN, MANAGER, or EMPLOYEE"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Hash password before saving
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(role);

        // =========================
        // TEAM
        // =========================

        if (request.getTeamId() != null) {

            Team team = teamRepository
                    .findById(request.getTeamId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Team not found with id: "
                                            + request.getTeamId()
                            )
                    );

            user.setTeam(team);
        }

        // =========================
        // MANAGER
        // =========================

        if (request.getManagerId() != null) {

            User manager = userRepository
                    .findById(request.getManagerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Manager not found with id: "
                                            + request.getManagerId()
                            )
                    );

            if (!"MANAGER".equals(manager.getRole())) {

                throw new IllegalArgumentException(
                        "Selected user is not a manager"
                );
            }

            user.setManager(manager);
        }

        // =========================
        // EMPLOYEE RULES
        // =========================
        if ("EMPLOYEE".equals(role)) {
            validateEmployeeAssignment(user);
        }

        return userRepository.save(user);
    }

    // =========================
    // UPDATE USER
    // =========================

    public User updateUser(
            Integer id,
            UserRequest request) {

        User existingUser =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        // =========================
        // EMAIL
        // =========================

        if (!existingUser.getEmail()
                .equals(request.getEmail())) {

            if (userRepository.existsByEmail(
                    request.getEmail())) {

                throw new RuntimeException(
                        "Email already exists"
                );
            }

            existingUser.setEmail(
                    request.getEmail()
            );
        }

        // =========================
        // BASIC DETAILS
        // =========================

        existingUser.setName(
                request.getName()
        );

        // =========================
        // PASSWORD
        // =========================

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        // =========================
        // ROLE
        // =========================

        String role =
                request.getRole()
                        .trim()
                        .toUpperCase();

        if (!role.equals("ADMIN")
                && !role.equals("MANAGER")
                && !role.equals("EMPLOYEE")) {

            throw new IllegalArgumentException(
                    "Role must be ADMIN, MANAGER, or EMPLOYEE"
            );
        }

        existingUser.setRole(role);

        // =========================
        // TEAM
        // =========================

        if (request.getTeamId() != null) {

            Team team = teamRepository
                    .findById(request.getTeamId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Team not found with id: "
                                            + request.getTeamId()
                            )
                    );

            existingUser.setTeam(team);

        } else {

            existingUser.setTeam(null);
        }

        // =========================
        // MANAGER
        // =========================

        if (request.getManagerId() != null) {

            User manager = userRepository
                    .findById(request.getManagerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Manager not found with id: "
                                            + request.getManagerId()
                            )
                    );

            if (!"MANAGER".equals(manager.getRole())) {

                throw new IllegalArgumentException(
                        "Selected user is not a manager"
                );
            }

            existingUser.setManager(manager);

        } else {

            existingUser.setManager(null);
        }

        // =========================
        // EMPLOYEE RULES
        // =========================
        if ("EMPLOYEE".equals(role)) {
            validateEmployeeAssignment(existingUser);
        }

        return userRepository.save(existingUser);
    }

    // =========================
    // EMPLOYEE TEAM / MANAGER CHECK
    // =========================
    // An employee may be unassigned (no manager yet). If a manager is
    // chosen, the manager must belong to a team, and the employee must be
    // in that same team (it is filled in automatically when left empty).
    private void validateEmployeeAssignment(User employee) {

        User manager = employee.getManager();

        if (manager == null) {
            return;
        }

        if (manager.getTeam() == null) {
            throw new IllegalArgumentException(
                    "Manager must belong to a team"
            );
        }

        if (employee.getTeam() == null) {
            employee.setTeam(manager.getTeam());
            return;
        }

        if (!employee.getTeam().getId()
                .equals(manager.getTeam().getId())) {
            throw new IllegalArgumentException(
                    "Employee and manager must belong to the same team"
            );
        }
    }

    // =========================
    // DELETE USER
    // =========================

    public void deleteUser(Integer id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        // Manager cannot be deleted
        // if employees are assigned to them.

        if ("MANAGER".equals(user.getRole())) {

            List<User> employees =
                    userRepository
                            .findByManagerId(id);

            if (!employees.isEmpty()) {

                throw new RuntimeException(
                        "Cannot delete manager with assigned employees"
                );
            }
        }

        userRepository.delete(user);
    }

    // =========================
    // CHECK USER ACCESS
    // =========================

    public boolean canAccessUser(
            User currentUser,
            User targetUser) {

        // Admin can access anyone
        if ("ADMIN".equals(currentUser.getRole())) {
            return true;
        }

        // User can access themselves
        if (currentUser.getId()
                .equals(targetUser.getId())) {

            return true;
        }

        // Manager can access their employees
        if ("MANAGER".equals(currentUser.getRole())) {

            return targetUser.getManager() != null
                    && targetUser.getManager()
                    .getId()
                    .equals(currentUser.getId());
        }

        return false;
    }

    // =========================
    // CHECK EMPLOYEE ACCESS
    // =========================

    public boolean canAccessEmployees(
            User currentUser,
            User manager) {

        // Admin can access any manager's employees
        if ("ADMIN".equals(currentUser.getRole())) {
            return true;
        }

        // Manager can only access their own employees
        if ("MANAGER".equals(currentUser.getRole())) {

            return currentUser.getId()
                    .equals(manager.getId());
        }

        return false;
    }
}