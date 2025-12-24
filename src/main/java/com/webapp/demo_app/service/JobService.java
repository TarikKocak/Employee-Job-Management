package com.webapp.demo_app.service;

import com.webapp.demo_app.model.MevcutIs;
import com.webapp.demo_app.model.TamamlananIs;
import com.webapp.demo_app.repository.MevcutIsRepository;
import com.webapp.demo_app.repository.TamamlananIsRepository;
import com.webapp.demo_app.repository.EmployeeRepository;
import com.webapp.demo_app.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class JobService {


    private final MevcutIsRepository mevcutIsRepository;
    private final TamamlananIsRepository tamamlananIsRepository;
    private final EmployeeRepository employeeRepository;
    private final AvailabilityService availabilityService;

    public JobService(MevcutIsRepository mevcutIsRepository,
                      TamamlananIsRepository tamamlananIsRepository,
                      EmployeeRepository employeeRepository,
                      AvailabilityService availabilityService) {
        this.mevcutIsRepository = mevcutIsRepository;
        this.tamamlananIsRepository = tamamlananIsRepository;
        this.employeeRepository = employeeRepository;
        this.availabilityService = availabilityService;
    }

    public List<MevcutIs> getMevcutIsler(Long employeeId) {
        return mevcutIsRepository.findByEmployeeIdOrderByTarihAsc(employeeId);
    }

    public List<TamamlananIs> getTamamlananIsler(Long employeeId) {
        return tamamlananIsRepository.findByEmployeeId(employeeId);
    }

    public MevcutIs getMevcutIsById(Long id) {
        return mevcutIsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mevcut is bulunamadı"));
    }

    @Transactional
    public MevcutIs updateWriteOnlyFields(Long jobId, Double sure, Integer bahsis,
                                          Boolean kartVerildi, Boolean yorumKartiVerildi,
                                          Boolean fotoAtildi) {
        MevcutIs mevcutIs = getMevcutIsById(jobId);
        mevcutIs.setSure(sure);
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

        if (mevcutIs.getSure() == null ||
                mevcutIs.getBahsis() == null ||
                mevcutIs.getKartVerildi() == null ||
                mevcutIs.getYorumKartiVerildi() == null ||
                mevcutIs.getFotoAtildi() == null) {
            throw new IncompleteJobException("Lütfen tüm alanları doldurunuz.");
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
}
