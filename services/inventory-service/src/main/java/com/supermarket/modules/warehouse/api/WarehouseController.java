package com.supermarket.modules.warehouse.api;

import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.application.service.AdjustmentService;
import com.supermarket.modules.warehouse.application.service.StockCountService;
import com.supermarket.modules.warehouse.application.service.TransferService;
import com.supermarket.modules.warehouse.application.service.WarehouseService;
import com.supermarket.modules.warehouse.domain.TransferStatus;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final TransferService transferService;
    private final StockCountService stockCountService;
    private final AdjustmentService adjustmentService;

    @GetMapping
    public ApiResponse<PageResponse<WarehouseDtos.WarehouseResponse>> listWarehouses(
            @RequestParam UUID organizationId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(warehouseService.list(organizationId, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<WarehouseDtos.WarehouseResponse> getWarehouse(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(warehouseService.get(organizationId, id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDtos.WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseDtos.WarehouseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(warehouseService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<WarehouseDtos.WarehouseResponse> updateWarehouse(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseDtos.WarehouseUpdateRequest request) {
        return ApiResponse.success(warehouseService.update(organizationId, id, request));
    }

    @GetMapping("/transfers")
    public ApiResponse<PageResponse<WarehouseDtos.TransferResponse>> listTransfers(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) TransferStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(transferService.list(organizationId, status, pageable)));
    }

    @GetMapping("/transfers/{id}")
    public ApiResponse<WarehouseDtos.TransferResponse> getTransfer(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(transferService.get(organizationId, id));
    }

    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<WarehouseDtos.TransferResponse>> createTransfer(
            @Valid @RequestBody WarehouseDtos.TransferCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transferService.create(request)));
    }

    @PostMapping("/transfers/{id}/ship")
    public ApiResponse<WarehouseDtos.TransferResponse> shipTransfer(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(transferService.ship(organizationId, id));
    }

    @PostMapping("/transfers/{id}/receive")
    public ApiResponse<WarehouseDtos.TransferResponse> receiveTransfer(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(transferService.receive(organizationId, id));
    }

    @PostMapping("/transfers/{id}/cancel")
    public ApiResponse<WarehouseDtos.TransferResponse> cancelTransfer(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(transferService.cancel(organizationId, id));
    }

    @GetMapping("/stock-counts")
    public ApiResponse<PageResponse<WarehouseDtos.StockCountResponse>> listStockCounts(
            @RequestParam UUID organizationId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(stockCountService.list(organizationId, pageable)));
    }

    @PostMapping("/stock-counts")
    public ResponseEntity<ApiResponse<WarehouseDtos.StockCountResponse>> createStockCount(
            @Valid @RequestBody WarehouseDtos.StockCountCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(stockCountService.create(request)));
    }

    @PostMapping("/stock-counts/{id}/start")
    public ApiResponse<WarehouseDtos.StockCountResponse> startStockCount(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(stockCountService.start(organizationId, id));
    }

    @PostMapping("/stock-counts/{id}/complete")
    public ApiResponse<WarehouseDtos.StockCountResponse> completeStockCount(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(stockCountService.complete(organizationId, id));
    }

    @GetMapping("/adjustments")
    public ApiResponse<PageResponse<WarehouseDtos.AdjustmentResponse>> listAdjustments(
            @RequestParam UUID organizationId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(adjustmentService.list(organizationId, pageable)));
    }

    @PostMapping("/adjustments")
    public ResponseEntity<ApiResponse<WarehouseDtos.AdjustmentResponse>> createAdjustment(
            @Valid @RequestBody WarehouseDtos.AdjustmentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adjustmentService.create(request)));
    }

    @PostMapping("/adjustments/{id}/apply")
    public ApiResponse<WarehouseDtos.AdjustmentResponse> applyAdjustment(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(adjustmentService.apply(organizationId, id));
    }
}
