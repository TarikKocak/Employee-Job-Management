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


    public LocalDate getWeekMonday(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
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

    public boolean
    validateMinimumAvailabilityPerWeek(Collection<String> slotKeys) {

        // 🔹 slotları haftalara ayır
        Map<LocalDate, List<String>> slotsByWeek = new HashMap<>();

        for (String key : slotKeys) {
            String[] parts = key.split("_");
            LocalDate date = LocalDate.parse(parts[0]);

            LocalDate monday = getWeekMonday(date);

            slotsByWeek
                    .computeIfAbsent(monday, d -> new ArrayList<>())
                    .add(key);
        }

        // 🔹 her hafta için kural kontrolü
        for (List<String> weekSlots : slotsByWeek.values()) {

            if (!validateSingleWeek(weekSlots)) {
                return false; // herhangi bir hafta kuralı bozarsa → FAIL
            }
        }

        return true;
    }

    private boolean validateSingleWeek(Collection<String> slotKeys) {

        Map<LocalDate, List<Integer>> byDate = new HashMap<>();

        for (String key : slotKeys) {
            String[] parts = key.split("_");
            LocalDate date = LocalDate.parse(parts[0]);
            Integer hour = Integer.parseInt(parts[1]);

            byDate
                    .computeIfAbsent(date, d -> new ArrayList<>())
                    .add(hour);
        }

        int validDayCount = 0;

        for (List<Integer> hours : byDate.values()) {
            Collections.sort(hours);

            int current = 1;
            int max = 1;

            for (int i = 1; i < hours.size(); i++) {
                if (hours.get(i) == hours.get(i - 1) + 1) {
                    current++;
                    max = Math.max(max, current);
                } else {
                    current = 1;
                }
            }

            if (max >= 5) {
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
    public void
    blockAvailabilityForJob(Long employeeId, LocalDate date, LocalTime startTime, double duration){
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

    /*
    public Map<String, Map<String, Integer>>
    getOverlappingAvailabilityForWeek(LocalDate monday) {

        LocalDate end = monday.plusDays(6);

        List<Employee> employees = employeeRepository.findAll();

        List<AvailabilitySlot> slots =
                availabilitySlotRepository.findByDateBetween(monday, end);

        // employeeId_date_hour -> status
        Map<String, Integer> slotIndex = new HashMap<>();
        for (AvailabilitySlot slot : slots) {
            String key = slot.getEmployee().getId()
                    + "_" + slot.getDate()
                    + "_" + slot.getHour();
            slotIndex.put(key, slot.getStatus());
        }

        Map<String, Map<String, Integer>> result = new HashMap<>();

        for (Employee emp : employees) {
            for (LocalDate d = monday; !d.isAfter(end); d = d.plusDays(1)) {
                for (Integer hour : getHours()) {

                    String cellKey = d + "_" + hour;
                    String empKey = emp.getId() + "_" + d + "_" + hour;

                    int status = slotIndex.getOrDefault(empKey, 0);

                    result
                            .computeIfAbsent(cellKey, k -> new LinkedHashMap<>())
                            .put(emp.getName(), status);
                }
            }
        }

        return result;
    }

     */

    public Map<String, Integer>
    buildAvailableCountMap(Map<String, Map<String, Integer>> overlapMap) {

        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : overlapMap.entrySet()) {

            int count = 0;
            for (Integer status : entry.getValue().values()) {
                if (status != null && status == 1) {
                    count++;
                }
            }

            result.put(entry.getKey(), count);
        }

        return result;
    }

    //-------------------------------------
    //  NEW FILTERED TABLE AND LIST LOGIC
    //-------------------------------------

    public Map<String, Map<String, Integer>>
    getOverlappingAvailabilityForWeek(
            LocalDate monday,
            List<Employee> employees,
            Integer minHours
    ) {
        LocalDate end = monday.plusDays(6);
        return buildOverlappingAvailability(
                monday, end, employees, minHours);
    }

    private Map<String, Map<String, Integer>>
    buildOverlappingAvailability(
            LocalDate monday,
            LocalDate end,
            List<Employee> employees,
            Integer minHours
    ) {
        List<AvailabilitySlot> slots =
                availabilitySlotRepository
                        .findByDateBetween(monday, end);

        // employeeId_date_hour -> status
        Map<String, Integer> slotIndex = new HashMap<>();
        for (AvailabilitySlot slot : slots) {
            String key =
                    slot.getEmployee().getId() + "_" +
                            slot.getDate() + "_" +
                            slot.getHour();
            slotIndex.put(key, slot.getStatus());
        }

        Map<String, Map<String, Integer>> result = new HashMap<>();

        Set<String> validEmployeeDays = null;

        if (minHours != null) {
            validEmployeeDays =
                    findValidEmployeeDays(
                            employees, monday, end, minHours, slotIndex
                    );
        }

        for (Employee emp : employees) {
            for (LocalDate d = monday; !d.isAfter(end); d = d.plusDays(1)) {
                for (Integer hour : getHours()) {

                    String cellKey = d + "_" + hour;
                    String empKey = emp.getId() + "_" + d + "_" + hour;

                    boolean validDay =
                            minHours == null ||
                                    validEmployeeDays.contains(emp.getId() + "_" + d);

                    int status = validDay
                            ? slotIndex.getOrDefault(empKey, 0)
                            : 0;

                    result
                            .computeIfAbsent(cellKey, k -> new LinkedHashMap<>())
                            .put(emp.getName(), status);
                }
            }
        }

        return result;
    }


    //-----------------//
    // MIN TIME FILTER //
    //-----------------//

    public List<Employee> filterEmployeesByMinAdjacentHours(
            List<Employee> employees,
            int minHours
    ) {
        LocalDate start = getNextWeekMonday();
        LocalDate end = start.plusDays(13); // 2 weeks (matches UI)

        List<AvailabilitySlot> slots =
                availabilitySlotRepository.findByDateBetween(start, end);

        // employeeId -> date -> list of available hours
        Map<Long, Map<LocalDate, List<Integer>>> map = new HashMap<>();

        for (AvailabilitySlot slot : slots) {
            if (slot.getStatus() != 1) continue;

            map
                    .computeIfAbsent(slot.getEmployee().getId(), k -> new HashMap<>())
                    .computeIfAbsent(slot.getDate(), d -> new ArrayList<>())
                    .add(slot.getHour());
        }

        return employees.stream()
                .filter(emp ->
                        hasMinAdjacentHours(map.get(emp.getId()), minHours)
                )
                .toList();
    }

    private boolean hasMinAdjacentHours(
            Map<LocalDate, List<Integer>> byDate,
            int minHours
    ) {
        if (byDate == null) return false;

        for (List<Integer> hours : byDate.values()) {
            Collections.sort(hours);

            int count = 1;
            for (int i = 1; i < hours.size(); i++) {
                if (hours.get(i) == hours.get(i - 1) + 1) {
                    count++;
                    if (count >= minHours) return true;
                } else {
                    count = 1;
                }
            }
        }
        return false;
    }

    private Set<String> findValidEmployeeDays(
            List<Employee> employees,
            LocalDate monday,
            LocalDate end,
            int minHours,
            Map<String, Integer> slotIndex
    ) {
        Set<String> validDays = new HashSet<>();

        for (Employee emp : employees) {
            for (LocalDate d = monday; !d.isAfter(end); d = d.plusDays(1)) {

                int maxBlock = 0;
                int current = 0;

                for (int hour = 7; hour <= 16; hour++) {
                    String key = emp.getId() + "_" + d + "_" + hour;

                    if (slotIndex.getOrDefault(key, 0) == 1) {
                        current++;
                        maxBlock = Math.max(maxBlock, current);
                    } else {
                        current = 0;
                    }
                }

                if (maxBlock >= minHours) {
                    // employeeId_date
                    validDays.add(emp.getId() + "_" + d);
                }
            }
        }
        return validDays;
    }


}
