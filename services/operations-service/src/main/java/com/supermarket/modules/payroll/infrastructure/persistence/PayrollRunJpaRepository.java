package com.supermarket.modules.payroll.infrastructure.persistence;

import com.supermarket.modules.payroll.domain.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayrollRunJpaRepository extends JpaRepository<PayrollRun, UUID> {
    List<PayrollRun> findByOrganizationIdOrderByPeriodStartDesc(UUID organizationId);
}
