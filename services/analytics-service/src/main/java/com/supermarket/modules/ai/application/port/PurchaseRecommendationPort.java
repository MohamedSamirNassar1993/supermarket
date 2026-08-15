package com.supermarket.modules.ai.application.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Port for purchase recommendation engines (Phase 19).
 */
public interface PurchaseRecommendationPort {

    /**
     * Recommend purchase quantities for products at a branch.
     *
     * @param organizationId tenant scope
     * @param branchId       branch to restock
     * @return ranked recommendations with suggested order quantities
     */
    List<PurchaseRecommendation> recommend(UUID organizationId, UUID branchId);

    record PurchaseRecommendation(
            UUID productId,
            String sku,
            String productName,
            BigDecimal currentStock,
            BigDecimal suggestedQuantity,
            String reason) {
    }
}
