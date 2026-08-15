package com.supermarket.modules.payroll.infrastructure.persistence;

import com.supermarket.modules.payroll.domain.PayrollLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayrollLineJpaRepository extends JpaRepository<PayrollLine, UUID> {
    List<PayrollLine> findByPayrollRunId(UUID payrollRunId);
}
