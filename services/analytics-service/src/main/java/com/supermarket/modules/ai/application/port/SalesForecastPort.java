package com.supermarket.modules.ai.application.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Port for sales forecasting integrations (Phase 19).
 * Implementations may use ML models, external APIs, or heuristic engines.
 */
public interface SalesForecastPort {

    /**
     * Forecast sales for a branch over a date range.
     *
     * @param organizationId tenant scope
     * @param branchId       branch scope (nullable for org-wide forecast)
     * @param from           forecast period start
     * @param to             forecast period end
     * @return list of daily/period forecasts with predicted revenue and confidence
     */
    List<ForecastPoint> forecast(UUID organizationId, UUID branchId, LocalDate from, LocalDate to);

    record ForecastPoint(LocalDate date, BigDecimal predictedRevenue, double confidence) {
    }
}
