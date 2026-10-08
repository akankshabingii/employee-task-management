package com.akanksha.employee_task_management.dto;

public class ManagerOption {

    private Integer id;
    private String name;
    private Integer teamId;

    public ManagerOption() {
    }

    public ManagerOption(Integer id, String name, Integer teamId) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
}
