package com.supermarket.modules.payroll.infrastructure.persistence;

import com.supermarket.modules.payroll.domain.EmployeeLoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeLoanJpaRepository extends JpaRepository<EmployeeLoan, UUID> {
    List<EmployeeLoan> findByEmployeeId(UUID employeeId);
}
