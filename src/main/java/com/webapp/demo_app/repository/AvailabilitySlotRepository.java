package com.webapp.demo_app.repository;
import com.webapp.demo_app.model.AvailabilitySlot;
import com.webapp.demo_app.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long>{

    // All slots for a specific employee and date range
    List<AvailabilitySlot> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate start, LocalDate end);

    // Record for a single cell (date + hour)
    AvailabilitySlot findByEmployeeIdAndDateAndHour(Long employeeId, LocalDate date, Integer hour);

    // To pull all slots (for weekly scroll)
    List<AvailabilitySlot> findByEmployeeId(Long employeeId);

    void deleteByEmployeeIdAndDateBetween(
            Long employeeId,
            LocalDate start,
            LocalDate end
    );

    void deleteByEmployeeIdAndStatus(Long employeeId, Integer status);
}
