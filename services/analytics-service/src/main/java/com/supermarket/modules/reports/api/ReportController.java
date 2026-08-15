package com.supermarket.modules.reports.api;

import com.supermarket.modules.reports.application.ReportService;
import com.supermarket.modules.reports.domain.ReportJob;
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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/jobs")
    public ApiResponse<ReportJob> submitJob(@Valid @RequestBody ReportJob job) {
        return ApiResponse.success(reportService.submitJob(job));
    }

    @GetMapping("/jobs/{id}")
    public ApiResponse<ReportJob> getJob(@PathVariable UUID id) {
        return ApiResponse.success(reportService.getJob(id));
    }

    @GetMapping("/jobs/queued")
    public ApiResponse<List<ReportJob>> listQueued() {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(reportService.listQueued(orgId));
    }
}
