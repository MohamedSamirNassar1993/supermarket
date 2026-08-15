package com.supermarket.modules.payroll.infrastructure.persistence;

import com.supermarket.modules.payroll.domain.SalaryAdvance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalaryAdvanceJpaRepository extends JpaRepository<SalaryAdvance, UUID> {
    List<SalaryAdvance> findByEmployeeId(UUID employeeId);
}
