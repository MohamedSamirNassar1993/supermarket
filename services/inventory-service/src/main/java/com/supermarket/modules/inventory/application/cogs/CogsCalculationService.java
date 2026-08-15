package com.supermarket.modules.inventory.application.cogs;

import com.supermarket.modules.inventory.domain.ValuationMethod;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CogsCalculationService {

    private final FifoCogsStrategy fifoCogsStrategy;
    private final LifoCogsStrategy lifoCogsStrategy;
    private final WeightedAverageCogsStrategy weightedAverageCogsStrategy;

    public CogsResult calculate(ValuationMethod method, List<StockBatchEntity> batches, BigDecimal quantity) {
        return resolveStrategy(method).calculate(batches, quantity);
    }

    private CogsStrategy resolveStrategy(ValuationMethod method) {
        Map<ValuationMethod, CogsStrategy> strategies = new EnumMap<>(ValuationMethod.class);
        strategies.put(ValuationMethod.FIFO, fifoCogsStrategy);
        strategies.put(ValuationMethod.LIFO, lifoCogsStrategy);
        strategies.put(ValuationMethod.WEIGHTED_AVERAGE, weightedAverageCogsStrategy);
        return strategies.getOrDefault(method, fifoCogsStrategy);
    }
}
