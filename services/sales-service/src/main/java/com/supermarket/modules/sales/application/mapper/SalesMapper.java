package com.supermarket.modules.sales.application.mapper;

import com.supermarket.modules.sales.application.dto.PromotionResponse;
import com.supermarket.modules.sales.application.dto.PromotionRuleResponse;
import com.supermarket.modules.sales.application.dto.SalesInvoiceResponse;
import com.supermarket.modules.sales.application.dto.SalesLineBatchResponse;
import com.supermarket.modules.sales.application.dto.SalesLineResponse;
import com.supermarket.modules.sales.application.dto.SalesPaymentResponse;
import com.supermarket.modules.sales.application.dto.SalesReturnResponse;
import com.supermarket.modules.sales.domain.Promotion;
import com.supermarket.modules.sales.domain.PromotionRule;
import com.supermarket.modules.sales.domain.PromotionType;
import com.supermarket.modules.sales.domain.SaleStatus;
import com.supermarket.modules.sales.domain.SaleType;
import com.supermarket.modules.sales.domain.SalesInvoice;
import com.supermarket.modules.sales.domain.SalesLine;
import com.supermarket.modules.sales.domain.SalesLineBatch;
import com.supermarket.modules.sales.domain.SalesPayment;
import com.supermarket.modules.sales.domain.SalesReturn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SalesMapper {

    @Mapping(target = "saleType", expression = "java(saleTypeName(invoice.getSaleType()))")
    @Mapping(target = "status", expression = "java(statusName(invoice.getStatus()))")
    @Mapping(target = "lines", source = "lines")
    @Mapping(target = "payments", source = "payments")
    SalesInvoiceResponse toInvoiceResponse(SalesInvoice invoice);

    @Mapping(target = "batches", source = "batches")
    SalesLineResponse toLineResponse(SalesLine line);

    SalesLineBatchResponse toBatchResponse(SalesLineBatch batch);

    SalesPaymentResponse toPaymentResponse(SalesPayment payment);

    @Mapping(target = "status", expression = "java(statusName(salesReturn.getStatus()))")
    SalesReturnResponse toReturnResponse(SalesReturn salesReturn);

    @Mapping(target = "promotionType", expression = "java(promoTypeName(promotion.getPromotionType()))")
    @Mapping(target = "rules", source = "rules")
    PromotionResponse toPromotionResponse(Promotion promotion);

    PromotionRuleResponse toRuleResponse(PromotionRule rule);

    List<SalesLineResponse> mapLines(List<SalesLine> lines);

    default String saleTypeName(SaleType type) {
        return type != null ? type.name() : null;
    }

    default String statusName(SaleStatus status) {
        return status != null ? status.name() : null;
    }

    default String promoTypeName(PromotionType type) {
        return type != null ? type.name() : null;
    }
}
