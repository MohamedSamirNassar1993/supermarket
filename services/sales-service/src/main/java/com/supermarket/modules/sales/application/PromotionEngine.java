package com.supermarket.modules.sales.application;

import com.supermarket.modules.sales.domain.Promotion;
import com.supermarket.modules.sales.domain.PromotionRule;
import com.supermarket.modules.sales.domain.PromotionType;
import com.supermarket.modules.sales.domain.SalesLine;
import com.supermarket.modules.sales.infrastructure.PromotionRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionEngine {

    private final PromotionRepository promotionRepository;

    public PromotionResult applyPromotions(UUID organizationId, List<SalesLine> lines, BigDecimal cartSubtotal) {
        List<Promotion> promotions = promotionRepository.findActivePromotions(organizationId, Instant.now());
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<UUID> appliedPromotionIds = new ArrayList<>();

        for (Promotion promotion : promotions) {
            BigDecimal promoDiscount = evaluatePromotion(promotion, lines, cartSubtotal);
            if (promoDiscount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(promoDiscount);
                appliedPromotionIds.add(promotion.getId());
                if (!promotion.isStackable()) {
                    break;
                }
            }
        }

        return PromotionResult.builder()
                .totalDiscount(totalDiscount)
                .appliedPromotionIds(appliedPromotionIds)
                .build();
    }

    private BigDecimal evaluatePromotion(Promotion promotion, List<SalesLine> lines, BigDecimal cartSubtotal) {
        BigDecimal discount = BigDecimal.ZERO;
        for (PromotionRule rule : promotion.getRules()) {
            discount = discount.add(evaluateRule(promotion.getPromotionType(), rule, lines, cartSubtotal));
        }
        return discount;
    }

    private BigDecimal evaluateRule(PromotionType type, PromotionRule rule,
                                   List<SalesLine> lines, BigDecimal cartSubtotal) {
        return switch (type) {
            case PERCENT_OFF -> applyPercentOff(rule, lines);
            case FIXED_OFF -> applyFixedOff(rule, lines, cartSubtotal);
            case MIN_AMOUNT_OFF -> applyMinAmountOff(rule, cartSubtotal);
            case BUY_X_GET_Y -> BigDecimal.ZERO;
        };
    }

    private BigDecimal applyPercentOff(PromotionRule rule, List<SalesLine> lines) {
        BigDecimal discount = BigDecimal.ZERO;
        for (SalesLine line : lines) {
            if (rule.getProductId() == null || rule.getProductId().equals(line.getProductId())) {
                if (rule.getMinQuantity() == null || line.getQuantity().compareTo(rule.getMinQuantity()) >= 0) {
                    BigDecimal lineAmount = line.getUnitPrice().multiply(line.getQuantity());
                    discount = discount.add(lineAmount.multiply(rule.getDiscountValue())
                            .divide(BigDecimal.valueOf(100)));
                    line.setPromotionId(rule.getPromotion().getId());
                }
            }
        }
        return discount;
    }

    private BigDecimal applyFixedOff(PromotionRule rule, List<SalesLine> lines, BigDecimal cartSubtotal) {
        if (rule.getMinAmount() != null && cartSubtotal.compareTo(rule.getMinAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        if (rule.getProductId() != null) {
            boolean hasProduct = lines.stream().anyMatch(l -> l.getProductId().equals(rule.getProductId()));
            return hasProduct ? rule.getDiscountValue() : BigDecimal.ZERO;
        }
        return rule.getDiscountValue();
    }

    private BigDecimal applyMinAmountOff(PromotionRule rule, BigDecimal cartSubtotal) {
        if (rule.getMinAmount() != null && cartSubtotal.compareTo(rule.getMinAmount()) >= 0) {
            if ("PERCENT".equalsIgnoreCase(rule.getDiscountType())) {
                return cartSubtotal.multiply(rule.getDiscountValue()).divide(BigDecimal.valueOf(100));
            }
            return rule.getDiscountValue();
        }
        return BigDecimal.ZERO;
    }

    @Getter
    @Builder
    public static class PromotionResult {
        private BigDecimal totalDiscount;
        private List<UUID> appliedPromotionIds;
    }
}
