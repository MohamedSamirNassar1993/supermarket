package com.supermarket.modules.inventory.api;

import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.application.service.InventoryMovementService;
import com.supermarket.modules.inventory.application.service.InventoryQueryService;
import com.supermarket.modules.inventory.application.service.OrganizationSettingsService;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryMovementService movementService;
    private final InventoryQueryService queryService;
    private final OrganizationSettingsService settingsService;

    @GetMapping("/settings")
    public ApiResponse<InventoryDtos.SettingsResponse> getSettings(@RequestParam UUID organizationId) {
        return ApiResponse.success(settingsService.get(organizationId));
    }

    @PutMapping("/settings")
    public ApiResponse<InventoryDtos.SettingsResponse> updateSettings(
            @RequestParam UUID organizationId,
            @Valid @RequestBody InventoryDtos.SettingsUpdateRequest request) {
        return ApiResponse.success(settingsService.update(organizationId, request));
    }

    @PostMapping("/stock-in")
    public ResponseEntity<ApiResponse<InventoryDtos.MovementResponse>> stockIn(
            @Valid @RequestBody InventoryDtos.StockInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movementService.recordStockIn(request)));
    }

    @PostMapping("/stock-out")
    public ResponseEntity<ApiResponse<InventoryDtos.MovementResponse>> stockOut(
            @Valid @RequestBody InventoryDtos.StockOutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movementService.recordStockOut(request)));
    }

    @GetMapping("/movements")
    public ApiResponse<PageResponse<InventoryDtos.MovementResponse>> listMovements(
            @RequestParam UUID organizationId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(queryService.listMovements(organizationId, pageable)));
    }

    @GetMapping("/stock-level")
    public ApiResponse<InventoryDtos.StockLevelResponse> stockLevel(
            @RequestParam UUID warehouseId,
            @RequestParam UUID productId,
            @RequestParam(required = false) UUID variantId) {
        return ApiResponse.success(queryService.getStockLevel(warehouseId, productId, variantId));
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<InventoryDtos.LowStockAlertResponse>> lowStock(@RequestParam UUID organizationId) {
        return ApiResponse.success(queryService.getLowStockAlerts(organizationId));
    }

    @GetMapping("/expiring")
    public ApiResponse<List<InventoryDtos.ExpiringBatchResponse>> expiring(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) Integer days) {
        return ApiResponse.success(queryService.getExpiringBatches(organizationId, days));
    }
}
