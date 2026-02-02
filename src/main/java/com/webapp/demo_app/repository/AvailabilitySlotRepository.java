package com.webapp.demo_app.repository;
import com.webapp.demo_app.model.AvailabilitySlot;
import com.webapp.demo_app.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long>{

    // All slots for a specific employee and date range
    List<AvailabilitySlot> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate start, LocalDate end);

    // Record for a single cell (date + hour)
    AvailabilitySlot findByEmployeeIdAndDateAndHour(Long employeeId, LocalDate date, Integer hour);

    // To pull all slots (for weekly scroll)
    List<AvailabilitySlot> findByEmployeeId(Long employeeId);




    void deleteByEmployeeIdAndStatusIn(Long employeeId, Collection<Integer> statuses);

    // FOR OVERLAPPING AVAILABILITY SLOT
    List<AvailabilitySlot> findByDateBetween(
            LocalDate start,
            LocalDate end
    );

    List<AvailabilitySlot> findByDateBetweenAndStatus(LocalDate start, LocalDate end, Integer status);

    @Query("""
    SELECT COUNT(DISTINCT a.date)
    FROM AvailabilitySlot a
    WHERE a.employee.id = :employeeId
      AND a.status = 2
      AND a.date BETWEEN :start AND :end
    """)
    int countAssignedJobDaysInWeek(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    // For expired Availability slot remove scheduling
    @Modifying
    @Transactional
    void deleteByDateBefore(LocalDate date);

}
