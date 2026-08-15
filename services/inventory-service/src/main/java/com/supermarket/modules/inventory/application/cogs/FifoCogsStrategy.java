package com.supermarket.modules.inventory.application.cogs;

import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class FifoCogsStrategy implements CogsStrategy {

    private static final int SCALE = 4;

    @Override
    public CogsResult calculate(List<StockBatchEntity> availableBatches, BigDecimal quantityRequested) {
        List<StockBatchEntity> sorted = availableBatches.stream()
                .sorted(Comparator.comparing(StockBatchEntity::getReceivedAt))
                .toList();
        return allocate(sorted, quantityRequested);
    }

    static CogsResult allocate(List<StockBatchEntity> sortedBatches, BigDecimal quantityRequested) {
        BigDecimal remaining = quantityRequested;
        BigDecimal totalCost = BigDecimal.ZERO;
        List<CogsAllocation> allocations = new ArrayList<>();

        for (StockBatchEntity batch : sortedBatches) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal batchQty = batch.getRemainingQuantity();
            if (batchQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal allocatedQty = remaining.min(batchQty);
            BigDecimal lineCost = allocatedQty.multiply(batch.getUnitCost()).setScale(SCALE, RoundingMode.HALF_UP);
            allocations.add(CogsAllocation.builder()
                    .batchId(batch.getId())
                    .batchNumber(batch.getBatchNumber())
                    .quantity(allocatedQty)
                    .unitCost(batch.getUnitCost())
                    .totalCost(lineCost)
                    .build());
            totalCost = totalCost.add(lineCost);
            remaining = remaining.subtract(allocatedQty);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("Insufficient stock to fulfill quantity: " + quantityRequested);
        }

        BigDecimal avgCost = totalCost.divide(quantityRequested, SCALE, RoundingMode.HALF_UP);
        return CogsResult.builder()
                .totalQuantity(quantityRequested)
                .totalCost(totalCost)
                .averageUnitCost(avgCost)
                .allocations(allocations)
                .build();
    }
}
