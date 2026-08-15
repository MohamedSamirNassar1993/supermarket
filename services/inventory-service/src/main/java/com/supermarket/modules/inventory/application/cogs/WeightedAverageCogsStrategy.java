package com.supermarket.modules.inventory.application.cogs;

import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class WeightedAverageCogsStrategy implements CogsStrategy {

    private static final int SCALE = 4;

    @Override
    public CogsResult calculate(List<StockBatchEntity> availableBatches, BigDecimal quantityRequested) {
        BigDecimal totalAvailable = availableBatches.stream()
                .map(StockBatchEntity::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(quantityRequested) < 0) {
            throw new BusinessRuleException("Insufficient stock to fulfill quantity: " + quantityRequested);
        }

        BigDecimal totalValue = availableBatches.stream()
                .map(b -> b.getRemainingQuantity().multiply(b.getUnitCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageUnitCost = totalAvailable.compareTo(BigDecimal.ZERO) > 0
                ? totalValue.divide(totalAvailable, SCALE, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal totalCost = quantityRequested.multiply(averageUnitCost).setScale(SCALE, RoundingMode.HALF_UP);

        CogsAllocation allocation = CogsAllocation.builder()
                .quantity(quantityRequested)
                .unitCost(averageUnitCost)
                .totalCost(totalCost)
                .build();

        return CogsResult.builder()
                .totalQuantity(quantityRequested)
                .totalCost(totalCost)
                .averageUnitCost(averageUnitCost)
                .allocations(List.of(allocation))
                .build();
    }
}
