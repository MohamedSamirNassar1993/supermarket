package com.supermarket.modules.sales.application;

import com.supermarket.modules.sales.application.dto.PromotionRequest;
import com.supermarket.modules.sales.application.dto.PromotionResponse;
import com.supermarket.modules.sales.application.mapper.SalesMapper;
import com.supermarket.modules.sales.domain.Promotion;
import com.supermarket.modules.sales.domain.PromotionRule;
import com.supermarket.modules.sales.infrastructure.PromotionRepository;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final SalesMapper salesMapper;

    @Transactional(readOnly = true)
    public List<PromotionResponse> list(UUID organizationId) {
        return promotionRepository.findActivePromotions(organizationId, Instant.now()).stream()
                .map(salesMapper::toPromotionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PromotionResponse get(UUID organizationId, UUID id) {
        return salesMapper.toPromotionResponse(requirePromotion(organizationId, id));
    }

    @Transactional
    public PromotionResponse create(UUID organizationId, PromotionRequest request) {
        promotionRepository.findByOrganizationIdAndCode(organizationId, request.getCode())
                .ifPresent(p -> {
                    throw new BusinessException(ErrorCode.CONFLICT, "Promotion code already exists");
                });

        Promotion promotion = new Promotion();
        promotion.setOrganizationId(organizationId);
        promotion.setCode(request.getCode().trim().toUpperCase());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        promotion.setStackable(request.getStackable() != null && request.getStackable());
        promotion.setActive(request.getActive() == null || request.getActive());
        promotion.setCreatedBy(TenantContext.getActor());
        promotion.setUpdatedBy(TenantContext.getActor());

        for (var ruleReq : request.getRules()) {
            PromotionRule rule = new PromotionRule();
            rule.setPromotion(promotion);
            rule.setRuleType(ruleReq.getRuleType());
            rule.setProductId(ruleReq.getProductId());
            rule.setMinQuantity(ruleReq.getMinQuantity());
            rule.setMinAmount(ruleReq.getMinAmount());
            rule.setDiscountType(ruleReq.getDiscountType());
            rule.setDiscountValue(ruleReq.getDiscountValue());
            rule.setFreeProductId(ruleReq.getFreeProductId());
            rule.setFreeQuantity(ruleReq.getFreeQuantity());
            promotion.getRules().add(rule);
        }

        return salesMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    @Transactional
    public void deactivate(UUID organizationId, UUID id) {
        Promotion promotion = requirePromotion(organizationId, id);
        promotion.setActive(false);
        promotion.setUpdatedBy(TenantContext.getActor());
        promotionRepository.save(promotion);
    }

    public Promotion requirePromotion(UUID organizationId, UUID id) {
        return promotionRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Promotion not found"));
    }
}
