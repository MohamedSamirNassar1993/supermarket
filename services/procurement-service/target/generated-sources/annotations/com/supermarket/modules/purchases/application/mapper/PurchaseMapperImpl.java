package com.supermarket.modules.purchases.application.mapper;

import com.supermarket.modules.purchases.application.dto.GoodsReceiptLineResponse;
import com.supermarket.modules.purchases.application.dto.GoodsReceiptResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseLineResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseOrderResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseQuotationResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseReturnLineResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseReturnResponse;
import com.supermarket.modules.purchases.application.dto.SupplierPaymentResponse;
import com.supermarket.modules.purchases.domain.GoodsReceipt;
import com.supermarket.modules.purchases.domain.GoodsReceiptLine;
import com.supermarket.modules.purchases.domain.PurchaseOrder;
import com.supermarket.modules.purchases.domain.PurchaseOrderLine;
import com.supermarket.modules.purchases.domain.PurchaseQuotation;
import com.supermarket.modules.purchases.domain.PurchaseQuotationLine;
import com.supermarket.modules.purchases.domain.PurchaseReturn;
import com.supermarket.modules.purchases.domain.PurchaseReturnLine;
import com.supermarket.modules.purchases.domain.SupplierPayment;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-11T02:21:19+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class PurchaseMapperImpl implements PurchaseMapper {

    @Override
    public PurchaseQuotationResponse toQuotationResponse(PurchaseQuotation quotation) {
        if ( quotation == null ) {
            return null;
        }

        PurchaseQuotationResponse.PurchaseQuotationResponseBuilder purchaseQuotationResponse = PurchaseQuotationResponse.builder();

        purchaseQuotationResponse.lines( mapQuotationLines( quotation.getLines() ) );
        purchaseQuotationResponse.id( quotation.getId() );
        purchaseQuotationResponse.branchId( quotation.getBranchId() );
        purchaseQuotationResponse.supplierId( quotation.getSupplierId() );
        purchaseQuotationResponse.quotationNumber( quotation.getQuotationNumber() );
        purchaseQuotationResponse.quotationDate( quotation.getQuotationDate() );
        purchaseQuotationResponse.validUntil( quotation.getValidUntil() );
        purchaseQuotationResponse.currencyCode( quotation.getCurrencyCode() );
        purchaseQuotationResponse.subtotal( quotation.getSubtotal() );
        purchaseQuotationResponse.taxAmount( quotation.getTaxAmount() );
        purchaseQuotationResponse.totalAmount( quotation.getTotalAmount() );
        purchaseQuotationResponse.notes( quotation.getNotes() );
        purchaseQuotationResponse.createdAt( quotation.getCreatedAt() );

        purchaseQuotationResponse.status( statusName(quotation.getStatus()) );

        return purchaseQuotationResponse.build();
    }

    @Override
    public PurchaseLineResponse toQuotationLineResponse(PurchaseQuotationLine line) {
        if ( line == null ) {
            return null;
        }

        PurchaseLineResponse.PurchaseLineResponseBuilder purchaseLineResponse = PurchaseLineResponse.builder();

        purchaseLineResponse.quantity( line.getQuantity() );
        purchaseLineResponse.id( line.getId() );
        purchaseLineResponse.productId( line.getProductId() );
        purchaseLineResponse.lineNumber( line.getLineNumber() );
        purchaseLineResponse.unitPrice( line.getUnitPrice() );
        purchaseLineResponse.taxRate( line.getTaxRate() );
        purchaseLineResponse.lineTotal( line.getLineTotal() );
        purchaseLineResponse.notes( line.getNotes() );

        purchaseLineResponse.receivedQuantity( new BigDecimal( "0" ) );

        return purchaseLineResponse.build();
    }

    @Override
    public PurchaseOrderResponse toOrderResponse(PurchaseOrder order) {
        if ( order == null ) {
            return null;
        }

        PurchaseOrderResponse.PurchaseOrderResponseBuilder purchaseOrderResponse = PurchaseOrderResponse.builder();

        purchaseOrderResponse.lines( mapOrderLines( order.getLines() ) );
        purchaseOrderResponse.id( order.getId() );
        purchaseOrderResponse.branchId( order.getBranchId() );
        purchaseOrderResponse.supplierId( order.getSupplierId() );
        purchaseOrderResponse.quotationId( order.getQuotationId() );
        purchaseOrderResponse.orderNumber( order.getOrderNumber() );
        purchaseOrderResponse.orderDate( order.getOrderDate() );
        purchaseOrderResponse.expectedDate( order.getExpectedDate() );
        purchaseOrderResponse.currencyCode( order.getCurrencyCode() );
        purchaseOrderResponse.subtotal( order.getSubtotal() );
        purchaseOrderResponse.taxAmount( order.getTaxAmount() );
        purchaseOrderResponse.totalAmount( order.getTotalAmount() );
        purchaseOrderResponse.receivedAmount( order.getReceivedAmount() );
        purchaseOrderResponse.notes( order.getNotes() );
        purchaseOrderResponse.createdAt( order.getCreatedAt() );

        purchaseOrderResponse.status( statusName(order.getStatus()) );

        return purchaseOrderResponse.build();
    }

    @Override
    public PurchaseLineResponse toOrderLineResponse(PurchaseOrderLine line) {
        if ( line == null ) {
            return null;
        }

        PurchaseLineResponse.PurchaseLineResponseBuilder purchaseLineResponse = PurchaseLineResponse.builder();

        purchaseLineResponse.quantity( line.getOrderedQuantity() );
        purchaseLineResponse.id( line.getId() );
        purchaseLineResponse.productId( line.getProductId() );
        purchaseLineResponse.lineNumber( line.getLineNumber() );
        purchaseLineResponse.receivedQuantity( line.getReceivedQuantity() );
        purchaseLineResponse.unitPrice( line.getUnitPrice() );
        purchaseLineResponse.taxRate( line.getTaxRate() );
        purchaseLineResponse.lineTotal( line.getLineTotal() );
        purchaseLineResponse.notes( line.getNotes() );

        return purchaseLineResponse.build();
    }

    @Override
    public GoodsReceiptResponse toReceiptResponse(GoodsReceipt receipt) {
        if ( receipt == null ) {
            return null;
        }

        GoodsReceiptResponse.GoodsReceiptResponseBuilder goodsReceiptResponse = GoodsReceiptResponse.builder();

        goodsReceiptResponse.lines( goodsReceiptLineListToGoodsReceiptLineResponseList( receipt.getLines() ) );
        goodsReceiptResponse.id( receipt.getId() );
        goodsReceiptResponse.orderId( receipt.getOrderId() );
        goodsReceiptResponse.receiptNumber( receipt.getReceiptNumber() );
        goodsReceiptResponse.receiptDate( receipt.getReceiptDate() );
        goodsReceiptResponse.notes( receipt.getNotes() );

        goodsReceiptResponse.status( statusName(receipt.getStatus()) );

        return goodsReceiptResponse.build();
    }

    @Override
    public GoodsReceiptLineResponse toReceiptLineResponse(GoodsReceiptLine line) {
        if ( line == null ) {
            return null;
        }

        GoodsReceiptLineResponse.GoodsReceiptLineResponseBuilder goodsReceiptLineResponse = GoodsReceiptLineResponse.builder();

        goodsReceiptLineResponse.id( line.getId() );
        goodsReceiptLineResponse.orderLineId( line.getOrderLineId() );
        goodsReceiptLineResponse.productId( line.getProductId() );
        goodsReceiptLineResponse.receivedQuantity( line.getReceivedQuantity() );
        goodsReceiptLineResponse.unitCost( line.getUnitCost() );
        goodsReceiptLineResponse.batchNumber( line.getBatchNumber() );
        goodsReceiptLineResponse.stockBatchId( line.getStockBatchId() );

        return goodsReceiptLineResponse.build();
    }

    @Override
    public SupplierPaymentResponse toPaymentResponse(SupplierPayment payment) {
        if ( payment == null ) {
            return null;
        }

        SupplierPaymentResponse.SupplierPaymentResponseBuilder supplierPaymentResponse = SupplierPaymentResponse.builder();

        supplierPaymentResponse.id( payment.getId() );
        supplierPaymentResponse.supplierId( payment.getSupplierId() );
        supplierPaymentResponse.orderId( payment.getOrderId() );
        supplierPaymentResponse.paymentNumber( payment.getPaymentNumber() );
        supplierPaymentResponse.paymentDate( payment.getPaymentDate() );
        supplierPaymentResponse.amount( payment.getAmount() );
        supplierPaymentResponse.paymentMethod( payment.getPaymentMethod() );
        supplierPaymentResponse.reference( payment.getReference() );

        return supplierPaymentResponse.build();
    }

    @Override
    public PurchaseReturnResponse toReturnResponse(PurchaseReturn purchaseReturn) {
        if ( purchaseReturn == null ) {
            return null;
        }

        PurchaseReturnResponse.PurchaseReturnResponseBuilder purchaseReturnResponse = PurchaseReturnResponse.builder();

        purchaseReturnResponse.lines( purchaseReturnLineListToPurchaseReturnLineResponseList( purchaseReturn.getLines() ) );
        purchaseReturnResponse.id( purchaseReturn.getId() );
        purchaseReturnResponse.supplierId( purchaseReturn.getSupplierId() );
        purchaseReturnResponse.returnNumber( purchaseReturn.getReturnNumber() );
        purchaseReturnResponse.returnDate( purchaseReturn.getReturnDate() );
        purchaseReturnResponse.totalAmount( purchaseReturn.getTotalAmount() );
        purchaseReturnResponse.reason( purchaseReturn.getReason() );

        purchaseReturnResponse.status( statusName(purchaseReturn.getStatus()) );

        return purchaseReturnResponse.build();
    }

    @Override
    public PurchaseReturnLineResponse toReturnLineResponse(PurchaseReturnLine line) {
        if ( line == null ) {
            return null;
        }

        PurchaseReturnLineResponse.PurchaseReturnLineResponseBuilder purchaseReturnLineResponse = PurchaseReturnLineResponse.builder();

        purchaseReturnLineResponse.id( line.getId() );
        purchaseReturnLineResponse.productId( line.getProductId() );
        purchaseReturnLineResponse.quantity( line.getQuantity() );
        purchaseReturnLineResponse.unitPrice( line.getUnitPrice() );
        purchaseReturnLineResponse.lineTotal( line.getLineTotal() );
        purchaseReturnLineResponse.stockBatchId( line.getStockBatchId() );

        return purchaseReturnLineResponse.build();
    }

    @Override
    public List<PurchaseLineResponse> mapQuotationLines(List<PurchaseQuotationLine> lines) {
        if ( lines == null ) {
            return null;
        }

        List<PurchaseLineResponse> list = new ArrayList<PurchaseLineResponse>( lines.size() );
        for ( PurchaseQuotationLine purchaseQuotationLine : lines ) {
            list.add( toQuotationLineResponse( purchaseQuotationLine ) );
        }

        return list;
    }

    @Override
    public List<PurchaseLineResponse> mapOrderLines(List<PurchaseOrderLine> lines) {
        if ( lines == null ) {
            return null;
        }

        List<PurchaseLineResponse> list = new ArrayList<PurchaseLineResponse>( lines.size() );
        for ( PurchaseOrderLine purchaseOrderLine : lines ) {
            list.add( toOrderLineResponse( purchaseOrderLine ) );
        }

        return list;
    }

    protected List<GoodsReceiptLineResponse> goodsReceiptLineListToGoodsReceiptLineResponseList(List<GoodsReceiptLine> list) {
        if ( list == null ) {
            return null;
        }

        List<GoodsReceiptLineResponse> list1 = new ArrayList<GoodsReceiptLineResponse>( list.size() );
        for ( GoodsReceiptLine goodsReceiptLine : list ) {
            list1.add( toReceiptLineResponse( goodsReceiptLine ) );
        }

        return list1;
    }

    protected List<PurchaseReturnLineResponse> purchaseReturnLineListToPurchaseReturnLineResponseList(List<PurchaseReturnLine> list) {
        if ( list == null ) {
            return null;
        }

        List<PurchaseReturnLineResponse> list1 = new ArrayList<PurchaseReturnLineResponse>( list.size() );
        for ( PurchaseReturnLine purchaseReturnLine : list ) {
            list1.add( toReturnLineResponse( purchaseReturnLine ) );
        }

        return list1;
    }
}
