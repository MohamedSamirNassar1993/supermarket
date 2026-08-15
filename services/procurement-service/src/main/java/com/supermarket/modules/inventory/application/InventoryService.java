package com.supermarket.modules.inventory.application;

import com.supermarket.integration.inventory.InventoryFeignClient;
import com.supermarket.modules.inventory.application.dto.AllocateStockRequest;
import com.supermarket.modules.inventory.application.dto.BatchAllocation;
import com.supermarket.modules.inventory.application.dto.ReceiveStockRequest;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryFeignClient inventoryFeignClient;

    public StockBatchEntity receiveStock(ReceiveStockRequest request) {
        return unwrap(inventoryFeignClient.receive(request));
    }

    public List<BatchAllocation> allocateForSale(AllocateStockRequest request) {
        return unwrap(inventoryFeignClient.allocate(request));
    }

    public StockBatchEntity returnToStock(UUID organizationId, UUID branchId, UUID productId, UUID batchId,
                                          BigDecimal quantity, String referenceType, UUID referenceId) {
        return unwrap(inventoryFeignClient.returnStock(
                organizationId, branchId, productId, batchId, quantity, referenceType, referenceId));
    }

    public void deductForPurchaseReturn(UUID batchId, BigDecimal quantity, String referenceType, UUID referenceId) {
        unwrap(inventoryFeignClient.deductReturn(batchId, quantity, referenceType, referenceId));
    }

    private <T> T unwrap(com.supermarket.shared.api.ApiResponse<T> response) {
        if (response == null || !response.isSuccess()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Inventory service call failed");
        }
        return response.getData();
    }
}
