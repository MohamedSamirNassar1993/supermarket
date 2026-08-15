package com.supermarket.modules.ai.application.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Port for product demand prediction (Phase 19).
 */
public interface DemandPredictionPort {

    /**
     * Predict demand for products over an upcoming period.
     */
    List<DemandPrediction> predict(UUID organizationId, UUID branchId, LocalDate from, LocalDate to);

    record DemandPrediction(
            UUID productId,
            String sku,
            LocalDate periodStart,
            LocalDate periodEnd,
            BigDecimal predictedUnits,
            double confidence) {
    }
}
