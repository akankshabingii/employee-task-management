package com.akanksha.employee_task_management.dto;

public class TeamResponse {

    private Integer id;
    private String name;

    public TeamResponse() {
    }

    public TeamResponse(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}