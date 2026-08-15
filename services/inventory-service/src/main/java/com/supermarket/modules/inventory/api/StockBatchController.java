package com.supermarket.modules.inventory.api;

import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.application.service.StockBatchService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stock-batches")
@RequiredArgsConstructor
public class StockBatchController {

    private final StockBatchService stockBatchService;

    @GetMapping
    public ApiResponse<PageResponse<InventoryDtos.StockBatchResponse>> list(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) UUID warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        if (warehouseId != null) {
            return ApiResponse.success(PageResponse.from(stockBatchService.listByWarehouse(warehouseId, pageable)));
        }
        return ApiResponse.success(PageResponse.from(stockBatchService.listByOrganization(organizationId, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<InventoryDtos.StockBatchResponse> get(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(stockBatchService.get(organizationId, id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryDtos.StockBatchResponse>> create(
            @Valid @RequestBody InventoryDtos.StockBatchCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(stockBatchService.create(request)));
    }
}
