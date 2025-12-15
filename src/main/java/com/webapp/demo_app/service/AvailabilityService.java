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
    public void saveAvailabilityForWeek(
            Long employeeId,
            Set<String> selectedSlots
    ) {
        Employee employee = employeeRepository.getReferenceById(employeeId);

        // SADECE YEŞİL SLOT'LARI TEMİZLE
        availabilitySlotRepository.deleteByEmployeeIdAndStatus(employeeId, 1);

        for (String key : selectedSlots) {
            String[] parts = key.split("_");
            LocalDate date = LocalDate.parse(parts[0]);
            Integer hour = Integer.parseInt(parts[1]);

            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setEmployee(employee);
            slot.setDate(date);
            slot.setHour(hour);
            slot.setStatus(1);

            availabilitySlotRepository.save(slot);
        }
    }


    // =========================
    // VALIDATION
    // =========================

    public boolean validateMinimumAvailability(Collection<String> slotKeys) {

        Map<LocalDate, List<Integer>> byDate = new HashMap<>();

        for (String key : slotKeys) {
            String[] parts = key.split("_");
            LocalDate date = LocalDate.parse(parts[0]);
            Integer hour = Integer.parseInt(parts[1]);

            byDate.computeIfAbsent(date, d -> new ArrayList<>()).add(hour);
        }

        int validDayCount = 0;

        for (List<Integer> hours : byDate.values()) {
            Collections.sort(hours);

            int currentBlock = 1;
            int maxBlock = 1;

            for (int i = 1; i < hours.size(); i++) {
                if (hours.get(i) == hours.get(i - 1) + 1) {
                    currentBlock++;
                    maxBlock = Math.max(maxBlock, currentBlock);
                } else {
                    currentBlock = 1;
                }
            }

            if (maxBlock >= 5) {
                validDayCount++;
            }

            if (validDayCount >= 4) {
                return true;
            }
        }

        return false;
    }



    // Hour row (7..16)
    public List<Integer> getHours() {
        return IntStream.rangeClosed(7, 16)
                .boxed()
                .collect(Collectors.toList());
    }


    public List<Integer> calculateBlockedHours(LocalTime startTime, double duration) {

        LocalTime endTime = startTime.plusMinutes((long) (duration * 60));

        int startHour = startTime.getHour(); // floor
        int endHour = endTime.getHour();     // floor

        List<Integer> hours = new ArrayList<>();
        for (int h = startHour; h <= endHour; h++) {
            hours.add(h);
        }

        return hours;
    }

    @Transactional
    public void blockAvailabilityForJob(Long employeeId, LocalDate date, LocalTime startTime, double duration){
        List<Integer> hours = calculateBlockedHours(startTime, duration);

        for(Integer hour : hours){
            AvailabilitySlot slot = availabilitySlotRepository.findByEmployeeIdAndDateAndHour(employeeId, date, hour);

            if(slot==null){
                slot  = new AvailabilitySlot();
                slot.setEmployee(employeeRepository.getReferenceById(employeeId));
                slot.setDate(date);
                slot.setHour(hour);
            }

            slot.setStatus(2);
            availabilitySlotRepository.save(slot);
        }

    }

    //

}
