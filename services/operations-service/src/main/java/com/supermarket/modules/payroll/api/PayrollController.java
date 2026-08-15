package com.supermarket.modules.payroll.api;

import com.supermarket.modules.payroll.application.PayrollService;
import com.supermarket.modules.payroll.domain.EmployeeLoan;
import com.supermarket.modules.payroll.domain.PayrollLine;
import com.supermarket.modules.payroll.domain.PayrollRun;
import com.supermarket.modules.payroll.domain.SalaryAdvance;
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
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/runs")
    public ApiResponse<List<PayrollRun>> listRuns() {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(payrollService.listRuns(orgId));
    }

    @PostMapping("/runs")
    public ApiResponse<PayrollRun> createRun(@Valid @RequestBody PayrollRun run) {
        return ApiResponse.success(payrollService.createRun(run));
    }

    @PostMapping("/runs/{id}/process")
    public ApiResponse<PayrollRun> processRun(@PathVariable UUID id) {
        return ApiResponse.success(payrollService.processRun(id));
    }

    @GetMapping("/runs/{id}/lines")
    public ApiResponse<List<PayrollLine>> getLines(@PathVariable UUID id) {
        return ApiResponse.success(payrollService.getLines(id));
    }

    @PostMapping("/advances")
    public ApiResponse<SalaryAdvance> createAdvance(@Valid @RequestBody SalaryAdvance advance) {
        return ApiResponse.success(payrollService.createAdvance(advance));
    }

    @PostMapping("/loans")
    public ApiResponse<EmployeeLoan> createLoan(@Valid @RequestBody EmployeeLoan loan) {
        return ApiResponse.success(payrollService.createLoan(loan));
    }
}
