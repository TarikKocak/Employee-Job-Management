package com.webapp.demo_app.service;

import com.webapp.demo_app.model.AvailabilitySlot;
import com.webapp.demo_app.model.Employee;
import com.webapp.demo_app.repository.AvailabilitySlotRepository;
import com.webapp.demo_app.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class AvailabilityService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final EmployeeRepository employeeRepository;

    public AvailabilityService(AvailabilitySlotRepository availabilitySlotRepository,
                               EmployeeRepository employeeRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.employeeRepository = employeeRepository;
    }


    //Find the first day of the week (Monday). Here we use next(DayOfWeek.MONDAY) for the Monday of the "week."
    public LocalDate getNextWeekMonday() {
        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return thisMonday.plusWeeks(1);
    }


    // week(7 days) from given monday
    public List<LocalDate> getWeekDates(LocalDate monday) {
        return IntStream.range(0, 7)
                .mapToObj(monday::plusDays)
                .collect(Collectors.toList());
    }

    // Listing all slots for a specific employee and week start
    public List<AvailabilitySlot> getWeekSlots(Long employeeId, LocalDate monday) {
        LocalDate end = monday.plusDays(6);
        return availabilitySlotRepository.findByEmployeeIdAndDateBetween(employeeId, monday, end);
    }

    // key: "yyyy-MM-dd_HH" -> value: status (0/1/2)
    public Map<String, Integer> buildStatusMap(List<AvailabilitySlot> slots) {
        Map<String, Integer> map = new HashMap<>();
        for (AvailabilitySlot slot : slots) {
            String key = slot.getDate() + "_" + slot.getHour();
            map.put(key, slot.getStatus());
        }
        return map;
    }


    @Transactional
    public void toggleSlot(Long employeeId, LocalDate date, Integer hour) {
        AvailabilitySlot slot =
                availabilitySlotRepository.findByEmployeeIdAndDateAndHour(employeeId, date, hour);

        if (slot == null) {
            AvailabilitySlot newSlot = new AvailabilitySlot();
            newSlot.setEmployee(employeeRepository.getReferenceById(employeeId));
            newSlot.setDate(date);
            newSlot.setHour(hour);
            newSlot.setStatus(1); // yeşil
            availabilitySlotRepository.save(newSlot);
            return;
        }

        // Eğer kırmızıysa employee değiştiremez!
        if (slot.getStatus() == 2) {
            return;   // hiçbir şey yapma!
        }

        // Eğer yeşil ise → gri (DELETE)
        if (slot.getStatus() == 1) {
            availabilitySlotRepository.delete(slot); // kayıt silinir = gri
        }
    }



    /**
     * Shifting records as weeks advance:
     * For example, when the week "next week" becomes the current week,
     * shift the availability dates in the past by +1 week.
     *
     * Thus, the green cells in week 3 will move to the position of week 2 one week later.
     */

    /*
    @Transactional
    public void rollWeeksIfNeeded(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalDate nextWeekMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        List<AvailabilitySlot> slots = availabilitySlotRepository.findByEmployeeId(employeeId);
        boolean changed = false;

        for (AvailabilitySlot slot : slots) {
            // If the slot date is before "nextWeekMonday" (that is, too far in the past to be displayed anymore) -> Move forward 1 week.
            while (slot.getDate().isBefore(nextWeekMonday)) {
                slot.setDate(slot.getDate().plusWeeks(1));
                changed = true;
            }
        }

        if (changed) {
            availabilitySlotRepository.saveAll(slots);
        }
    }
    */

    // Hour row (7..16)
    public List<Integer> getHours() {
        return IntStream.rangeClosed(7, 16)
                .boxed()
                .collect(Collectors.toList());
    }
}
