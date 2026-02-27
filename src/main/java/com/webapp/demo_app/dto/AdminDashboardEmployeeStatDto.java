package com.webapp.demo_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardEmployeeStatDto {
    private Long employeeId;
    private String employeeName;
    private double weeklyIncome;
    private double weeklyWorkHours;
    private double monthlyIncome;
    private double monthlyWorkHours;
}
