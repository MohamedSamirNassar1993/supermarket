package com.supermarket.modules.inventory.application.cogs;

import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;

import java.math.BigDecimal;
import java.util.List;

public interface CogsStrategy {

    CogsResult calculate(List<StockBatchEntity> availableBatches, BigDecimal quantityRequested);
}
