package com.supermarket.modules.ai.application.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Port for customer behavior analytics (Phase 19).
 */
public interface CustomerBehaviorPort {

    /**
     * Analyze customer purchase patterns and segments.
     */
    CustomerBehaviorInsight analyze(UUID organizationId, UUID customerId);

    record CustomerBehaviorInsight(
            UUID customerId,
            String segment,
            BigDecimal lifetimeValue,
            int purchaseFrequencyDays,
            List<String> topCategories,
            Map<String, Object> metadata) {
    }
}
