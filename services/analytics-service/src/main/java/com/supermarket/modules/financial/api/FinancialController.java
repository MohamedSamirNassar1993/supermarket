package com.supermarket.modules.financial.api;

import com.supermarket.modules.financial.application.FinancialService;
import com.supermarket.modules.financial.domain.CashFlowEntry;
import com.supermarket.modules.financial.domain.FinancialSnapshot;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    @GetMapping("/profit-loss")
    public ApiResponse<List<Map<String, Object>>> profitAndLoss(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        UUID branchId = BranchContext.getBranchId().orElse(null);
        return ApiResponse.success(financialService.getProfitAndLoss(orgId, branchId, from, to));
    }

    @GetMapping("/cogs")
    public ApiResponse<Map<String, Object>> cogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        UUID branchId = BranchContext.getBranchId().orElse(null);
        return ApiResponse.success(financialService.aggregateCogs(orgId, branchId, from, to));
    }

    @GetMapping("/cash-flow")
    public ApiResponse<List<CashFlowEntry>> cashFlow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(financialService.listCashFlow(orgId, from, to));
    }

    @PostMapping("/cash-flow")
    public ApiResponse<CashFlowEntry> recordCashFlow(@Valid @RequestBody CashFlowEntry entry) {
        return ApiResponse.success(financialService.recordCashFlow(entry));
    }

    @PostMapping("/snapshots")
    public ApiResponse<FinancialSnapshot> createSnapshot(@Valid @RequestBody FinancialSnapshot snapshot) {
        return ApiResponse.success(financialService.createSnapshot(snapshot));
    }

    @GetMapping("/snapshots")
    public ApiResponse<List<FinancialSnapshot>> listSnapshots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(financialService.listSnapshots(orgId, from, to));
    }
}
