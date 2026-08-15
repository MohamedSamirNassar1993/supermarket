package com.supermarket.modules.hr.api;

import com.supermarket.modules.hr.application.HrService;
import com.supermarket.modules.hr.domain.Attendance;
import com.supermarket.modules.hr.domain.Employee;
import com.supermarket.modules.hr.domain.LeaveRequest;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr")
@RequiredArgsConstructor
public class HrController {

    private final HrService hrService;

    @GetMapping("/employees")
    public ApiResponse<List<Employee>> listEmployees() {
        UUID branchId = BranchContext.getBranchId()
                .orElseThrow(() -> new IllegalArgumentException("X-Branch-Id header required"));
        return ApiResponse.success(hrService.listActiveEmployees(branchId));
    }

    @PostMapping("/employees")
    public ApiResponse<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        return ApiResponse.success(hrService.createEmployee(employee));
    }

    @GetMapping("/employees/{id}")
    public ApiResponse<Employee> getEmployee(@PathVariable UUID id) {
        return ApiResponse.success(hrService.getEmployee(id));
    }

    @PostMapping("/attendance")
    public ApiResponse<Attendance> recordAttendance(@Valid @RequestBody Attendance attendance) {
        return ApiResponse.success(hrService.recordAttendance(attendance));
    }

    @GetMapping("/employees/{id}/attendance")
    public ApiResponse<List<Attendance>> getAttendance(@PathVariable UUID id) {
        return ApiResponse.success(hrService.getAttendance(id));
    }

    @PostMapping("/leave-requests")
    public ApiResponse<LeaveRequest> requestLeave(@Valid @RequestBody LeaveRequest request) {
        return ApiResponse.success(hrService.requestLeave(request));
    }

    @PostMapping("/leave-requests/{id}/approve")
    public ApiResponse<LeaveRequest> approveLeave(@PathVariable UUID id) {
        return ApiResponse.success(hrService.approveLeave(id));
    }
}
