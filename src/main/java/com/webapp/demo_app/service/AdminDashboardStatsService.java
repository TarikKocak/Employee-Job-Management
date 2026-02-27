package com.webapp.demo_app.service;

import com.webapp.demo_app.dto.AdminDashboardEmployeeStatDto;
import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.model.TamamlananIs;
import com.webapp.demo_app.repository.TamamlananIsRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardStatsService {

    private final TamamlananIsRepository tamamlananIsRepository;

    public AdminDashboardStatsService(TamamlananIsRepository tamamlananIsRepository){
        this.tamamlananIsRepository = tamamlananIsRepository;
    }

    public List<AdminDashboardEmployeeStatDto> buildEmployeeStats(Collection<Employee> employees){
        if(employees.isEmpty()){
            return List.of();
        }

        List<Long> employeeIds = employees.stream().map(Employee::getId).toList();
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        LocalDate queryStart = startOfWeek.isBefore(startOfMonth) ? startOfWeek : startOfMonth;
        LocalDate queryEnd = endOfWeek.isAfter(endOfMonth) ? endOfWeek : endOfMonth;

        List<TamamlananIs> completedJobs = tamamlananIsRepository
                .findByTarihBetweenAndEmployeeIdIn(queryStart, queryEnd, employeeIds);

        Map<Long, AdminDashboardEmployeeStatDto> statMap = new LinkedHashMap<>();

        for (Employee employee : employees) {
            statMap.put(employee.getId(), new AdminDashboardEmployeeStatDto(
                    employee.getId(),
                    employee.getUsername(),
                    0,
                    0,
                    0,
                    0
            ));
        }

        for (TamamlananIs job : completedJobs) {
            AdminDashboardEmployeeStatDto stat = statMap.get(job.getEmployee().getId());
            if (stat == null || job.getTarih() == null) {
                continue;
            }

            double income = job.getUcret() == null ? 0 : job.getUcret();
            double workHours = job.getSure() == null ? 0 : job.getSure();

            if (!job.getTarih().isBefore(startOfWeek) && !job.getTarih().isAfter(endOfWeek)) {
                stat.setWeeklyIncome(stat.getWeeklyIncome() + income);
                stat.setWeeklyWorkHours(stat.getWeeklyWorkHours() + workHours);
            }

            if (!job.getTarih().isBefore(startOfMonth) && !job.getTarih().isAfter(endOfMonth)) {
                stat.setMonthlyIncome(stat.getMonthlyIncome() + income);
                stat.setMonthlyWorkHours(stat.getMonthlyWorkHours() + workHours);
            }
        }

        List<AdminDashboardEmployeeStatDto> result = new ArrayList<>(statMap.values());
        result.sort(Comparator.comparing(AdminDashboardEmployeeStatDto::getEmployeeName));
        return result;
    }
}



