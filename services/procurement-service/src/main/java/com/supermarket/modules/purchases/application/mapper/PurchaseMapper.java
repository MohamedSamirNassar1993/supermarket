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
import com.supermarket.modules.purchases.domain.PurchaseDocumentStatus;
import com.supermarket.modules.purchases.domain.PurchaseOrder;
import com.supermarket.modules.purchases.domain.PurchaseOrderLine;
import com.supermarket.modules.purchases.domain.PurchaseQuotation;
import com.supermarket.modules.purchases.domain.PurchaseQuotationLine;
import com.supermarket.modules.purchases.domain.PurchaseReturn;
import com.supermarket.modules.purchases.domain.PurchaseReturnLine;
import com.supermarket.modules.purchases.domain.SupplierPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PurchaseMapper {

    @Mapping(target = "status", expression = "java(statusName(quotation.getStatus()))")
    @Mapping(target = "lines", source = "lines")
    PurchaseQuotationResponse toQuotationResponse(PurchaseQuotation quotation);

    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "receivedQuantity", constant = "0")
    @Mapping(target = "id", source = "id")
    PurchaseLineResponse toQuotationLineResponse(PurchaseQuotationLine line);

    @Mapping(target = "status", expression = "java(statusName(order.getStatus()))")
    @Mapping(target = "lines", source = "lines")
    PurchaseOrderResponse toOrderResponse(PurchaseOrder order);

    @Mapping(target = "quantity", source = "orderedQuantity")
    PurchaseLineResponse toOrderLineResponse(PurchaseOrderLine line);

    @Mapping(target = "status", expression = "java(statusName(receipt.getStatus()))")
    @Mapping(target = "lines", source = "lines")
    GoodsReceiptResponse toReceiptResponse(GoodsReceipt receipt);

    GoodsReceiptLineResponse toReceiptLineResponse(GoodsReceiptLine line);

    SupplierPaymentResponse toPaymentResponse(SupplierPayment payment);

    @Mapping(target = "status", expression = "java(statusName(purchaseReturn.getStatus()))")
    @Mapping(target = "lines", source = "lines")
    PurchaseReturnResponse toReturnResponse(PurchaseReturn purchaseReturn);

    PurchaseReturnLineResponse toReturnLineResponse(PurchaseReturnLine line);

    List<PurchaseLineResponse> mapQuotationLines(List<PurchaseQuotationLine> lines);

    List<PurchaseLineResponse> mapOrderLines(List<PurchaseOrderLine> lines);

    default String statusName(PurchaseDocumentStatus status) {
        return status != null ? status.name() : null;
    }
}
