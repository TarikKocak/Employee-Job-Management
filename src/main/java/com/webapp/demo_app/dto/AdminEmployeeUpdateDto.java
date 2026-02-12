package com.webapp.demo_app.dto;

import lombok.Data;

@Data
public class AdminEmployeeUpdateDto {

    private String username;
    private String password;
    private String email;
    private Integer minDay;
    private Integer minHour;
}