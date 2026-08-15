package com.supermarket.modules.inventory.api;

import com.supermarket.modules.inventory.application.InventoryService;
import com.supermarket.modules.inventory.application.dto.AllocateStockRequest;
import com.supermarket.modules.inventory.application.dto.BatchAllocation;
import com.supermarket.modules.inventory.application.dto.ReceiveStockRequest;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/internal")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/receive")
    public ApiResponse<StockBatchEntity> receive(@RequestBody ReceiveStockRequest request) {
        return ApiResponse.success(inventoryService.receiveStock(request));
    }

    @PostMapping("/allocate")
    public ApiResponse<List<BatchAllocation>> allocate(@RequestBody AllocateStockRequest request) {
        return ApiResponse.success(inventoryService.allocateForSale(request));
    }

    @PostMapping("/return")
    public ApiResponse<StockBatchEntity> returnStock(
            @RequestParam UUID organizationId,
            @RequestParam UUID branchId,
            @RequestParam UUID productId,
            @RequestParam(required = false) UUID batchId,
            @RequestParam BigDecimal quantity,
            @RequestParam String referenceType,
            @RequestParam UUID referenceId) {
        return ApiResponse.success(inventoryService.returnToStock(
                organizationId, branchId, productId, batchId, quantity, referenceType, referenceId));
    }

    @PostMapping("/deduct-return")
    public ApiResponse<Void> deductReturn(
            @RequestParam UUID batchId,
            @RequestParam BigDecimal quantity,
            @RequestParam String referenceType,
            @RequestParam UUID referenceId) {
        inventoryService.deductForPurchaseReturn(batchId, quantity, referenceType, referenceId);
        return ApiResponse.success(null);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }
}
