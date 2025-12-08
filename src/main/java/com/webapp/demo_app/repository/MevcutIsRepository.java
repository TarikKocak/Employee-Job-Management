package com.webapp.demo_app.repository;

import com.webapp.demo_app.model.MevcutIs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MevcutIsRepository extends JpaRepository<MevcutIs, Long> {
    //List<MevcutIs> findByEmployeeId(Long employeeId);
    List<MevcutIs> findByEmployeeIdOrderByTarihAsc(Long employeeId);
}
