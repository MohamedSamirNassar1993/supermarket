package com.supermarket.modules.dashboard.api;

import com.supermarket.modules.dashboard.application.DashboardService;
import com.supermarket.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getAggregates() {
        return ApiResponse.success(dashboardService.getAggregates());
    }
}
