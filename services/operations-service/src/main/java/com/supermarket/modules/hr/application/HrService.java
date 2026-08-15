package com.supermarket.modules.hr.application;

import com.supermarket.modules.hr.domain.Attendance;
import com.supermarket.modules.hr.domain.Employee;
import com.supermarket.modules.hr.domain.LeaveRequest;
import com.supermarket.modules.hr.infrastructure.persistence.AttendanceJpaRepository;
import com.supermarket.modules.hr.infrastructure.persistence.EmployeeJpaRepository;
import com.supermarket.modules.hr.infrastructure.persistence.LeaveRequestJpaRepository;
import com.supermarket.shared.audit.AuditAction;
import com.supermarket.shared.audit.Audited;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrService {

    private final EmployeeJpaRepository employeeRepository;
    private final AttendanceJpaRepository attendanceRepository;
    private final LeaveRequestJpaRepository leaveRequestRepository;

    @Transactional(readOnly = true)
    public List<Employee> listActiveEmployees(UUID branchId) {
        return employeeRepository.findByBranchIdAndStatus(branchId, "ACTIVE");
    }

    @Transactional
    @Audited(entityType = "Employee", action = AuditAction.CREATE)
    public Employee createEmployee(Employee employee) {
        BranchContext.getOrganizationId().ifPresent(employee::setOrganizationId);
        BranchContext.getBranchId().ifPresent(employee::setBranchId);
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public Employee getEmployee(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    @Transactional
    @Audited(entityType = "Attendance", action = AuditAction.CREATE)
    public Attendance recordAttendance(Attendance attendance) {
        BranchContext.getBranchId().ifPresent(attendance::setBranchId);
        return attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendance(UUID employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    @Audited(entityType = "LeaveRequest", action = AuditAction.CREATE)
    public LeaveRequest requestLeave(LeaveRequest request) {
        return leaveRequestRepository.save(request);
    }

    @Transactional
    @Audited(entityType = "LeaveRequest", action = AuditAction.APPROVE)
    public LeaveRequest approveLeave(UUID id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found: " + id));
        request.setStatus("APPROVED");
        request.setApprovedBy(BranchContext.getActorId());
        request.setApprovedAt(Instant.now());
        return leaveRequestRepository.save(request);
    }
}
