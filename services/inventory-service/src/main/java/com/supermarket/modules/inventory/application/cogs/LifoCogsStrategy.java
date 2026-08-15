package com.supermarket.modules.inventory.application.cogs;

import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class LifoCogsStrategy implements CogsStrategy {

    @Override
    public CogsResult calculate(List<StockBatchEntity> availableBatches, BigDecimal quantityRequested) {
        List<StockBatchEntity> sorted = availableBatches.stream()
                .sorted(Comparator.comparing(StockBatchEntity::getReceivedAt).reversed())
                .toList();
        return FifoCogsStrategy.allocate(sorted, quantityRequested);
    }
}
