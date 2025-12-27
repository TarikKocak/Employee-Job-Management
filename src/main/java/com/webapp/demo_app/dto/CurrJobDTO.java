package com.webapp.demo_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class CurrJobDTO {

    private Long jobId;
    private String employeeName;
    private LocalDate date;
    private Integer hour;


}
