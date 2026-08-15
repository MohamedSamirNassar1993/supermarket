package com.supermarket.integration.inventory;

import com.supermarket.modules.inventory.application.dto.AllocateStockRequest;
import com.supermarket.modules.inventory.application.dto.BatchAllocation;
import com.supermarket.modules.inventory.application.dto.ReceiveStockRequest;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.shared.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {

    @PostMapping("/api/v1/inventory/internal/receive")
    ApiResponse<StockBatchEntity> receive(@RequestBody ReceiveStockRequest request);

    @PostMapping("/api/v1/inventory/internal/allocate")
    ApiResponse<List<BatchAllocation>> allocate(@RequestBody AllocateStockRequest request);

    @PostMapping("/api/v1/inventory/internal/return")
    ApiResponse<StockBatchEntity> returnStock(
            @RequestParam("organizationId") UUID organizationId,
            @RequestParam("branchId") UUID branchId,
            @RequestParam("productId") UUID productId,
            @RequestParam(value = "batchId", required = false) UUID batchId,
            @RequestParam("quantity") BigDecimal quantity,
            @RequestParam("referenceType") String referenceType,
            @RequestParam("referenceId") UUID referenceId);

    @PostMapping("/api/v1/inventory/internal/deduct-return")
    ApiResponse<Void> deductReturn(
            @RequestParam("batchId") UUID batchId,
            @RequestParam("quantity") BigDecimal quantity,
            @RequestParam("referenceType") String referenceType,
            @RequestParam("referenceId") UUID referenceId);
}
