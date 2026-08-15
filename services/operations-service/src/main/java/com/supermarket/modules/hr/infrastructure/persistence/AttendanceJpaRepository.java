package com.supermarket.modules.hr.infrastructure.persistence;

import com.supermarket.modules.hr.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByEmployeeId(UUID employeeId);
}
