package com.supermarket.modules.hr.infrastructure.persistence;

import com.supermarket.modules.hr.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByBranchIdAndStatus(UUID branchId, String status);
}
