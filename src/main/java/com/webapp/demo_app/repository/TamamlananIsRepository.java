package com.webapp.demo_app.repository;

import com.webapp.demo_app.model.TamamlananIs;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TamamlananIsRepository extends JpaRepository<TamamlananIs, Long> {
    List<TamamlananIs> findByEmployeeId(Long employeeId);

    @EntityGraph(attributePaths = "employee")
    List<TamamlananIs> findAllByOrderByTarihAsc();

    List<TamamlananIs> findByEmployeeIdOrderByTarihAsc(Long employeeId);
}
