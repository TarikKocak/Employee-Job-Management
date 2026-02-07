package com.webapp.demo_app.service;

import com.webapp.demo_app.dto.CurrJobDTO;
import com.webapp.demo_app.dto.TamamlananIsDto;
import com.webapp.demo_app.model.AvailabilitySlot;
import com.webapp.demo_app.model.MevcutIs;
import com.webapp.demo_app.model.TamamlananIs;
import com.webapp.demo_app.model.enums.Tur;
import com.webapp.demo_app.model.enums.UcretTahsilTipi;
import com.webapp.demo_app.repository.AvailabilitySlotRepository;
import com.webapp.demo_app.repository.MevcutIsRepository;
import com.webapp.demo_app.repository.TamamlananIsRepository;
import com.webapp.demo_app.repository.EmployeeRepository;
import com.webapp.demo_app.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class JobService {


    private final MevcutIsRepository mevcutIsRepository;
    private final TamamlananIsRepository tamamlananIsRepository;
    private final EmployeeRepository employeeRepository;
    private final AvailabilityService availabilityService;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public JobService(MevcutIsRepository mevcutIsRepository,
                      TamamlananIsRepository tamamlananIsRepository,
                      EmployeeRepository employeeRepository,
                      AvailabilityService availabilityService,
                      AvailabilitySlotRepository availabilitySlotRepository) {
        this.mevcutIsRepository = mevcutIsRepository;
        this.tamamlananIsRepository = tamamlananIsRepository;
        this.employeeRepository = employeeRepository;
        this.availabilityService = availabilityService;
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    public List<MevcutIs> getMevcutIsler(Long employeeId) {
        return mevcutIsRepository.findByEmployeeIdOrderByTarihAsc(employeeId);
    }

    //For employees
    public List<TamamlananIsDto> getTamamlananIsler(Long employeeId) {

        return tamamlananIsRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TamamlananIsDto toDto(TamamlananIs job) {

        TamamlananIsDto dto = new TamamlananIsDto();

        dto.setId(job.getId());
        dto.setTur(job.getTur());
        dto.setDuvarMontaji(job.getDuvarMontaji());
        dto.setIsAdresi(job.getIsAdresi());
        dto.setTanimi(job.getTanimi());
        dto.setTarih(job.getTarih());
        dto.setUcretTahsilTipi(job.getUcretTahsilTipi());
        dto.setSure(job.getSure());
        dto.setBahsis(job.getBahsis());
        dto.setKartVerildi(job.getKartVerildi());
        dto.setYorumKartiVerildi(job.getYorumKartiVerildi());
        dto.setFotoAtildi(job.getFotoAtildi());

        // Ucret(price) visible to employees when Tur and UcretTahsilTipi statements are satisfied

        if (job.getTur() == Tur.YALNIZ &&
                job.getUcretTahsilTipi() == UcretTahsilTipi.CASH) {

            dto.setUcret(job.getUcret() + "€");
        } else {
            dto.setUcret("No Info");
        }

        return dto;
    }

    //For only admins
    public List<TamamlananIs> getAllTamamlananIsler() {
        return tamamlananIsRepository.findAllByOrderByTarihAsc();
    }

    public MevcutIs getMevcutIsById(Long id) {
        return mevcutIsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mevcut is bulunamadı"));
    }

    @Transactional
    public MevcutIs updateWriteOnlyFields(
            Long jobId,
            LocalTime asilBaslanilanSaat,
            LocalTime bitisSaati,
            Integer bahsis,
            Boolean kartVerildi,
            Boolean yorumKartiVerildi,
            Boolean fotoAtildi
    ) {
        MevcutIs mevcutIs = getMevcutIsById(jobId);

        if (bitisSaati.isBefore(asilBaslanilanSaat)) {
            throw new IllegalArgumentException("Bitiş saati başlangıç saatinden önce olamaz");
        }
        double sureSaat = Duration
                .between(asilBaslanilanSaat, bitisSaati)
                .toMinutes() / 60.0;
        mevcutIs.setAsilBaslanilanSaat(asilBaslanilanSaat);
        mevcutIs.setBitisSaati(bitisSaati);
        mevcutIs.setSure(sureSaat);

        mevcutIs.setBahsis(bahsis);
        mevcutIs.setKartVerildi(kartVerildi);
        mevcutIs.setYorumKartiVerildi(yorumKartiVerildi);
        mevcutIs.setFotoAtildi(fotoAtildi);

        return mevcutIsRepository.save(mevcutIs);
    }

    public void submitJob(Long employeeId, Long jobId) {
        MevcutIs mevcutIs = getMevcutIsById(jobId);

        // Employee Controller (başkasının işini submit edemesin)
        if (!mevcutIs.getEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("Bu iş bu employee'e ait değil");
        }

        // check for write-only fields whether they are filled

        if (mevcutIs.getAsilBaslanilanSaat() == null ||
                mevcutIs.getBitisSaati() == null ||
                mevcutIs.getSure() == null ||
                mevcutIs.getBahsis() == null ||
                mevcutIs.getKartVerildi() == null ||
                mevcutIs.getYorumKartiVerildi() == null ||
                mevcutIs.getFotoAtildi() == null) {

            throw new IncompleteJobException(
                    "Lütfen işin başlangıç ve bitiş saatleri dahil tüm alanları doldurunuz."
            );
        }

        // Create data for TamamlananIs
        TamamlananIs tamamlananIs = new TamamlananIs();
        tamamlananIs.setEmployee(mevcutIs.getEmployee());
        tamamlananIs.setTur(mevcutIs.getTur());
        tamamlananIs.setDuvarMontaji(mevcutIs.getDuvarMontaji());
        tamamlananIs.setIsAdresi(mevcutIs.getIsAdresi());
        tamamlananIs.setTanimi(mevcutIs.getIsTanimi());
        tamamlananIs.setTarih(mevcutIs.getTarih());
        tamamlananIs.setUcret(mevcutIs.getUcret());
        tamamlananIs.setUcretTahsilTipi(mevcutIs.getUcretTahsilTipi());

        tamamlananIs.setAsilBaslanilanSaat(mevcutIs.getAsilBaslanilanSaat());
        tamamlananIs.setBitisSaati(mevcutIs.getBitisSaati());

        tamamlananIs.setSure(mevcutIs.getSure());

        tamamlananIs.setBahsis(mevcutIs.getBahsis());
        tamamlananIs.setKartVerildi(mevcutIs.getKartVerildi());
        tamamlananIs.setYorumKartiVerildi(mevcutIs.getYorumKartiVerildi());
        tamamlananIs.setFotoAtildi(mevcutIs.getFotoAtildi());

        tamamlananIsRepository.save(tamamlananIs);

        // delete mevcutIs
        mevcutIsRepository.delete(mevcutIs);
    }

    @Transactional
    public void assignJobToEmployee(Long employeeId, MevcutIs job) {

        Employee employee = employeeRepository.findById(employeeId).orElseThrow();

        job.setId(null);

        job.setEmployee(employee);

        //Writenoly fields should be null for employee
        job.setSure(null);
        job.setBahsis(null);
        job.setKartVerildi(null);
        job.setYorumKartiVerildi(null);
        job.setFotoAtildi(null);

        mevcutIsRepository.save(job);

        availabilityService.blockAvailabilityForJob(
                employeeId,
                job.getTarih(),
                job.getBaslangicSaati(),
                job.getTahminiSure()
        );
    }

    // if selected job type is COWORK, this code piece is necessary
    @Transactional
    public void assignJobToEmployees(
            Long mainEmployeeId,
            MevcutIs baseJob,
            Long otherEmployeeId
    ) {

        // always assign to main employee
        assignJobToEmployee(mainEmployeeId, cloneJob(baseJob));

        //  only COWORK allows multi-assign
        if (baseJob.getTur() != Tur.COWORK) {
            return;
        }

        if (otherEmployeeId == null) {
            return;
        }

        assignJobToEmployee(otherEmployeeId, cloneJob(baseJob));

    }

    private MevcutIs cloneJob(MevcutIs source) {
        MevcutIs job = new MevcutIs();

        job.setTur(source.getTur());
        job.setDuvarMontaji(source.getDuvarMontaji());
        job.setIsim(source.getIsim());
        job.setIsAdresi(source.getIsAdresi());
        job.setTelNo(source.getTelNo());
        job.setIsTanimi(source.getIsTanimi());
        job.setTarih(source.getTarih());
        job.setBaslangicSaati(source.getBaslangicSaati());
        job.setTahminiSure(source.getTahminiSure());
        job.setUcret(source.getUcret());
        job.setUcretTahsilTipi(source.getUcretTahsilTipi());

        return job;
    }



    public Map<String, List<CurrJobDTO>>
    getJobOverlapForWeek(LocalDate monday) {

        LocalDate end = monday.plusDays(6);

        // 1️⃣ occupied slots only
        List<AvailabilitySlot> occupiedSlots =
                availabilitySlotRepository
                        .findByDateBetweenAndStatus(monday, end, 2);

        // 2️⃣ current jobs
        List<MevcutIs> jobs = mevcutIsRepository.findAll();

        // employeeId_date → job
        Map<String, MevcutIs> jobIndex = new HashMap<>();
        for (MevcutIs job : jobs) {
            String key =
                    job.getEmployee().getId() + "_" + job.getTarih();
            jobIndex.put(key, job);
        }

        // date_hour → list of JobCellDTO
        Map<String, List<CurrJobDTO>> result = new HashMap<>();

        for (AvailabilitySlot slot : occupiedSlots) {

            String cellKey =
                    slot.getDate() + "_" + slot.getHour();

            String jobKey =
                    slot.getEmployee().getId() + "_" + slot.getDate();

            MevcutIs job = jobIndex.get(jobKey);
            if (job == null) continue;

            CurrJobDTO dto = new CurrJobDTO(
                    job.getId(),
                    job.getEmployee().getUsername(),
                    slot.getDate(),
                    slot.getHour()
            );

            result
                    .computeIfAbsent(cellKey, k -> new ArrayList<>())
                    .add(dto);
        }

        return result;
    }
    @Transactional
    public MevcutIs updateCurrentJobInfo(Long employeeId,
                                         Long jobId,
                                         MevcutIs updatedJob) {
        MevcutIs mevcutIs = getMevcutIsById(jobId);

        if (!mevcutIs.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Job does not belong to employee");
        }

        LocalDate oldDate = mevcutIs.getTarih();
        LocalTime oldStart = mevcutIs.getBaslangicSaati();
        Double oldDuration = mevcutIs.getTahminiSure();

        mevcutIs.setTur(updatedJob.getTur());
        mevcutIs.setDuvarMontaji(updatedJob.getDuvarMontaji());
        mevcutIs.setIsim(updatedJob.getIsim());
        mevcutIs.setIsAdresi(updatedJob.getIsAdresi());
        mevcutIs.setTelNo(updatedJob.getTelNo());
        mevcutIs.setIsTanimi(updatedJob.getIsTanimi());
        mevcutIs.setTarih(updatedJob.getTarih());
        mevcutIs.setBaslangicSaati(updatedJob.getBaslangicSaati());
        mevcutIs.setTahminiSure(updatedJob.getTahminiSure());
        mevcutIs.setUcret(updatedJob.getUcret());
        mevcutIs.setUcretTahsilTipi(updatedJob.getUcretTahsilTipi());

        MevcutIs savedJob = mevcutIsRepository.save(mevcutIs);

        if (oldDate != null
                && oldStart != null
                && oldDuration != null
                && (!Objects.equals(oldDate, savedJob.getTarih())
                || !Objects.equals(oldStart, savedJob.getBaslangicSaati())
                || !Objects.equals(oldDuration, savedJob.getTahminiSure()))) {
            availabilityService.unblockAvailabilityForJob(
                    employeeId,
                    oldDate,
                    oldStart,
                    oldDuration
            );
        }

        if (savedJob.getTarih() != null
                && savedJob.getBaslangicSaati() != null
                && savedJob.getTahminiSure() != null) {
            availabilityService.blockAvailabilityForJob(
                    employeeId,
                    savedJob.getTarih(),
                    savedJob.getBaslangicSaati(),
                    savedJob.getTahminiSure()
            );
        }

        return savedJob;
    }

    @Transactional
    public void deleteCurrentJob(Long employeeId, Long jobId) {
        MevcutIs mevcutIs = getMevcutIsById(jobId);

        if (!mevcutIs.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Job does not belong to employee");
        }

        if (mevcutIs.getTarih() != null
                && mevcutIs.getBaslangicSaati() != null
                && mevcutIs.getTahminiSure() != null) {
            availabilityService.unblockAvailabilityForJob(
                    employeeId,
                    mevcutIs.getTarih(),
                    mevcutIs.getBaslangicSaati(),
                    mevcutIs.getTahminiSure()
            );
        }

        mevcutIsRepository.delete(mevcutIs);
    }



}
