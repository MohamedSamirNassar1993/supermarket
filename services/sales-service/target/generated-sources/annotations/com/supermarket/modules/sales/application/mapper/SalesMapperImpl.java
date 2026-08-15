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
import com.supermarket.modules.sales.domain.SalesInvoice;
import com.supermarket.modules.sales.domain.SalesLine;
import com.supermarket.modules.sales.domain.SalesLineBatch;
import com.supermarket.modules.sales.domain.SalesPayment;
import com.supermarket.modules.sales.domain.SalesReturn;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-11T02:21:29+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class SalesMapperImpl implements SalesMapper {

    @Override
    public SalesInvoiceResponse toInvoiceResponse(SalesInvoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        SalesInvoiceResponse.SalesInvoiceResponseBuilder salesInvoiceResponse = SalesInvoiceResponse.builder();

        salesInvoiceResponse.lines( mapLines( invoice.getLines() ) );
        salesInvoiceResponse.payments( salesPaymentListToSalesPaymentResponseList( invoice.getPayments() ) );
        salesInvoiceResponse.id( invoice.getId() );
        salesInvoiceResponse.branchId( invoice.getBranchId() );
        salesInvoiceResponse.customerId( invoice.getCustomerId() );
        salesInvoiceResponse.invoiceNumber( invoice.getInvoiceNumber() );
        salesInvoiceResponse.invoiceDate( invoice.getInvoiceDate() );
        salesInvoiceResponse.subtotal( invoice.getSubtotal() );
        salesInvoiceResponse.discountAmount( invoice.getDiscountAmount() );
        salesInvoiceResponse.taxAmount( invoice.getTaxAmount() );
        salesInvoiceResponse.totalAmount( invoice.getTotalAmount() );
        salesInvoiceResponse.paidAmount( invoice.getPaidAmount() );
        salesInvoiceResponse.loyaltyPointsEarned( invoice.getLoyaltyPointsEarned() );

        salesInvoiceResponse.saleType( saleTypeName(invoice.getSaleType()) );
        salesInvoiceResponse.status( statusName(invoice.getStatus()) );

        return salesInvoiceResponse.build();
    }

    @Override
    public SalesLineResponse toLineResponse(SalesLine line) {
        if ( line == null ) {
            return null;
        }

        SalesLineResponse.SalesLineResponseBuilder salesLineResponse = SalesLineResponse.builder();

        salesLineResponse.batches( salesLineBatchListToSalesLineBatchResponseList( line.getBatches() ) );
        salesLineResponse.id( line.getId() );
        salesLineResponse.productId( line.getProductId() );
        salesLineResponse.lineNumber( line.getLineNumber() );
        salesLineResponse.quantity( line.getQuantity() );
        salesLineResponse.unitPrice( line.getUnitPrice() );
        salesLineResponse.discountAmount( line.getDiscountAmount() );
        salesLineResponse.lineTotal( line.getLineTotal() );
        salesLineResponse.promotionId( line.getPromotionId() );

        return salesLineResponse.build();
    }

    @Override
    public SalesLineBatchResponse toBatchResponse(SalesLineBatch batch) {
        if ( batch == null ) {
            return null;
        }

        SalesLineBatchResponse.SalesLineBatchResponseBuilder salesLineBatchResponse = SalesLineBatchResponse.builder();

        salesLineBatchResponse.stockBatchId( batch.getStockBatchId() );
        salesLineBatchResponse.quantity( batch.getQuantity() );

        return salesLineBatchResponse.build();
    }

    @Override
    public SalesPaymentResponse toPaymentResponse(SalesPayment payment) {
        if ( payment == null ) {
            return null;
        }

        SalesPaymentResponse.SalesPaymentResponseBuilder salesPaymentResponse = SalesPaymentResponse.builder();

        salesPaymentResponse.id( payment.getId() );
        salesPaymentResponse.paymentMethod( payment.getPaymentMethod() );
        salesPaymentResponse.amount( payment.getAmount() );
        salesPaymentResponse.reference( payment.getReference() );
        salesPaymentResponse.paidAt( payment.getPaidAt() );

        return salesPaymentResponse.build();
    }

    @Override
    public SalesReturnResponse toReturnResponse(SalesReturn salesReturn) {
        if ( salesReturn == null ) {
            return null;
        }

        SalesReturnResponse.SalesReturnResponseBuilder salesReturnResponse = SalesReturnResponse.builder();

        salesReturnResponse.id( salesReturn.getId() );
        salesReturnResponse.invoiceId( salesReturn.getInvoiceId() );
        salesReturnResponse.returnNumber( salesReturn.getReturnNumber() );
        salesReturnResponse.refundAmount( salesReturn.getRefundAmount() );
        salesReturnResponse.refundMethod( salesReturn.getRefundMethod() );

        salesReturnResponse.status( statusName(salesReturn.getStatus()) );

        return salesReturnResponse.build();
    }

    @Override
    public PromotionResponse toPromotionResponse(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }

        PromotionResponse.PromotionResponseBuilder promotionResponse = PromotionResponse.builder();

        promotionResponse.rules( promotionRuleListToPromotionRuleResponseList( promotion.getRules() ) );
        promotionResponse.id( promotion.getId() );
        promotionResponse.code( promotion.getCode() );
        promotionResponse.name( promotion.getName() );
        promotionResponse.startDate( promotion.getStartDate() );
        promotionResponse.endDate( promotion.getEndDate() );
        promotionResponse.priority( promotion.getPriority() );
        promotionResponse.stackable( promotion.isStackable() );
        promotionResponse.active( promotion.isActive() );

        promotionResponse.promotionType( promoTypeName(promotion.getPromotionType()) );

        return promotionResponse.build();
    }

    @Override
    public PromotionRuleResponse toRuleResponse(PromotionRule rule) {
        if ( rule == null ) {
            return null;
        }

        PromotionRuleResponse.PromotionRuleResponseBuilder promotionRuleResponse = PromotionRuleResponse.builder();

        promotionRuleResponse.id( rule.getId() );
        promotionRuleResponse.ruleType( rule.getRuleType() );
        promotionRuleResponse.productId( rule.getProductId() );
        promotionRuleResponse.minQuantity( rule.getMinQuantity() );
        promotionRuleResponse.minAmount( rule.getMinAmount() );
        promotionRuleResponse.discountType( rule.getDiscountType() );
        promotionRuleResponse.discountValue( rule.getDiscountValue() );

        return promotionRuleResponse.build();
    }

    @Override
    public List<SalesLineResponse> mapLines(List<SalesLine> lines) {
        if ( lines == null ) {
            return null;
        }

        List<SalesLineResponse> list = new ArrayList<SalesLineResponse>( lines.size() );
        for ( SalesLine salesLine : lines ) {
            list.add( toLineResponse( salesLine ) );
        }

        return list;
    }

    protected List<SalesPaymentResponse> salesPaymentListToSalesPaymentResponseList(List<SalesPayment> list) {
        if ( list == null ) {
            return null;
        }

        List<SalesPaymentResponse> list1 = new ArrayList<SalesPaymentResponse>( list.size() );
        for ( SalesPayment salesPayment : list ) {
            list1.add( toPaymentResponse( salesPayment ) );
        }

        return list1;
    }

    protected List<SalesLineBatchResponse> salesLineBatchListToSalesLineBatchResponseList(List<SalesLineBatch> list) {
        if ( list == null ) {
            return null;
        }

        List<SalesLineBatchResponse> list1 = new ArrayList<SalesLineBatchResponse>( list.size() );
        for ( SalesLineBatch salesLineBatch : list ) {
            list1.add( toBatchResponse( salesLineBatch ) );
        }

        return list1;
    }

    protected List<PromotionRuleResponse> promotionRuleListToPromotionRuleResponseList(List<PromotionRule> list) {
        if ( list == null ) {
            return null;
        }

        List<PromotionRuleResponse> list1 = new ArrayList<PromotionRuleResponse>( list.size() );
        for ( PromotionRule promotionRule : list ) {
            list1.add( toRuleResponse( promotionRule ) );
        }

        return list1;
    }
}
