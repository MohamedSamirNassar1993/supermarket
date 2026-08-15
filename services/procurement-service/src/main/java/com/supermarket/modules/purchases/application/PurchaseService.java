package com.supermarket.modules.purchases.application;

import com.supermarket.modules.inventory.application.InventoryService;
import com.supermarket.modules.inventory.application.dto.ReceiveStockRequest;
import com.supermarket.modules.products.application.ProductLookupService;
import com.supermarket.modules.purchases.application.dto.GoodsReceiptRequest;
import com.supermarket.modules.purchases.application.dto.GoodsReceiptResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseLineRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseOrderRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseOrderResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseQuotationRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseQuotationResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseReturnRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseReturnResponse;
import com.supermarket.modules.purchases.application.dto.SupplierPaymentRequest;
import com.supermarket.modules.purchases.application.dto.SupplierPaymentResponse;
import com.supermarket.modules.purchases.application.mapper.PurchaseMapper;
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
import com.supermarket.modules.purchases.infrastructure.GoodsReceiptRepository;
import com.supermarket.modules.purchases.infrastructure.PurchaseOrderRepository;
import com.supermarket.modules.purchases.infrastructure.PurchaseQuotationRepository;
import com.supermarket.modules.purchases.infrastructure.PurchaseReturnRepository;
import com.supermarket.modules.purchases.infrastructure.SupplierPaymentRepository;
import com.supermarket.modules.suppliers.application.SupplierService;
import com.supermarket.modules.suppliers.domain.SupplierTransactionType;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.DocumentNumberGenerator;
import com.supermarket.shared.domain.MoneyUtils;
import com.supermarket.shared.domain.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private static final String REF_GOODS_RECEIPT = "GOODS_RECEIPT";
    private static final String REF_PURCHASE_ORDER = "PURCHASE_ORDER";
    private static final String REF_PURCHASE_RETURN = "PURCHASE_RETURN";
    private static final String REF_SUPPLIER_PAYMENT = "SUPPLIER_PAYMENT";

    private final PurchaseQuotationRepository quotationRepository;
    private final PurchaseOrderRepository orderRepository;
    private final GoodsReceiptRepository receiptRepository;
    private final SupplierPaymentRepository paymentRepository;
    private final PurchaseReturnRepository returnRepository;
    private final PurchaseMapper purchaseMapper;
    private final SupplierService supplierService;
    private final ProductLookupService productLookupService;
    private final InventoryService inventoryService;
    private final DocumentNumberGenerator documentNumberGenerator;

    @Transactional
    public PurchaseQuotationResponse createQuotation(UUID organizationId, PurchaseQuotationRequest request) {
        supplierService.requireSupplier(organizationId, request.getSupplierId());
        PurchaseQuotation quotation = new PurchaseQuotation();
        quotation.setOrganizationId(organizationId);
        quotation.setBranchId(request.getBranchId());
        quotation.setSupplierId(request.getSupplierId());
        quotation.setQuotationNumber(documentNumberGenerator.next("PQ"));
        quotation.setStatus(PurchaseDocumentStatus.DRAFT);
        quotation.setQuotationDate(request.getQuotationDate() != null ? request.getQuotationDate() : LocalDate.now());
        quotation.setValidUntil(request.getValidUntil());
        quotation.setCurrencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode() : "USD");
        quotation.setNotes(request.getNotes());
        quotation.setCreatedBy(TenantContext.getActor());
        quotation.setUpdatedBy(TenantContext.getActor());

        int lineNum = 1;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        for (PurchaseLineRequest lineReq : request.getLines()) {
            productLookupService.requireProduct(organizationId, lineReq.getProductId());
            PurchaseQuotationLine line = buildQuotationLine(quotation, lineReq, lineNum++);
            quotation.getLines().add(line);
            subtotal = MoneyUtils.add(subtotal, line.getLineTotal());
            taxTotal = MoneyUtils.add(taxTotal, computeTax(line.getLineTotal(), line.getTaxRate()));
        }
        quotation.setSubtotal(subtotal);
        quotation.setTaxAmount(taxTotal);
        quotation.setTotalAmount(MoneyUtils.add(subtotal, taxTotal));
        return purchaseMapper.toQuotationResponse(quotationRepository.save(quotation));
    }

    @Transactional
    public PurchaseQuotationResponse approveQuotation(UUID organizationId, UUID quotationId) {
        PurchaseQuotation quotation = requireQuotation(organizationId, quotationId);
        if (quotation.getStatus() != PurchaseDocumentStatus.DRAFT
                && quotation.getStatus() != PurchaseDocumentStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.CONFLICT, "Quotation cannot be approved from status " + quotation.getStatus());
        }
        quotation.setStatus(PurchaseDocumentStatus.APPROVED);
        quotation.setUpdatedBy(TenantContext.getActor());
        return purchaseMapper.toQuotationResponse(quotationRepository.save(quotation));
    }

    @Transactional
    public PurchaseOrderResponse createOrderFromQuotation(UUID organizationId, UUID quotationId) {
        PurchaseQuotation quotation = requireQuotation(organizationId, quotationId);
        if (quotation.getStatus() != PurchaseDocumentStatus.APPROVED) {
            throw new BusinessException(ErrorCode.CONFLICT, "Quotation must be approved before creating order");
        }
        PurchaseOrderRequest request = new PurchaseOrderRequest();
        request.setBranchId(quotation.getBranchId());
        request.setSupplierId(quotation.getSupplierId());
        request.setQuotationId(quotationId);
        request.setCurrencyCode(quotation.getCurrencyCode());
        request.setNotes(quotation.getNotes());
        List<PurchaseLineRequest> lines = quotation.getLines().stream().map(l -> {
            PurchaseLineRequest lr = new PurchaseLineRequest();
            lr.setProductId(l.getProductId());
            lr.setQuantity(l.getQuantity());
            lr.setUnitPrice(l.getUnitPrice());
            lr.setTaxRate(l.getTaxRate());
            lr.setNotes(l.getNotes());
            return lr;
        }).toList();
        request.setLines(lines);
        return createOrder(organizationId, request);
    }

    @Transactional
    public PurchaseOrderResponse createOrder(UUID organizationId, PurchaseOrderRequest request) {
        supplierService.requireSupplier(organizationId, request.getSupplierId());
        PurchaseOrder order = new PurchaseOrder();
        order.setOrganizationId(organizationId);
        order.setBranchId(request.getBranchId());
        order.setSupplierId(request.getSupplierId());
        order.setQuotationId(request.getQuotationId());
        order.setOrderNumber(documentNumberGenerator.next("PO"));
        order.setStatus(PurchaseDocumentStatus.DRAFT);
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now());
        order.setExpectedDate(request.getExpectedDate());
        order.setCurrencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode() : "USD");
        order.setNotes(request.getNotes());
        order.setCreatedBy(TenantContext.getActor());
        order.setUpdatedBy(TenantContext.getActor());

        int lineNum = 1;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        for (PurchaseLineRequest lineReq : request.getLines()) {
            productLookupService.requireProduct(organizationId, lineReq.getProductId());
            PurchaseOrderLine line = buildOrderLine(order, lineReq, lineNum++);
            order.getLines().add(line);
            subtotal = MoneyUtils.add(subtotal, line.getLineTotal());
            taxTotal = MoneyUtils.add(taxTotal, computeTax(line.getLineTotal(), line.getTaxRate()));
        }
        order.setSubtotal(subtotal);
        order.setTaxAmount(taxTotal);
        order.setTotalAmount(MoneyUtils.add(subtotal, taxTotal));
        return purchaseMapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse submitOrder(UUID organizationId, UUID orderId) {
        PurchaseOrder order = requireOrder(organizationId, orderId);
        if (order.getStatus() != PurchaseDocumentStatus.DRAFT) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only draft orders can be submitted");
        }
        order.setStatus(PurchaseDocumentStatus.SUBMITTED);
        order.setUpdatedBy(TenantContext.getActor());
        return purchaseMapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse approveOrder(UUID organizationId, UUID orderId) {
        PurchaseOrder order = requireOrder(organizationId, orderId);
        if (order.getStatus() != PurchaseDocumentStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only submitted orders can be approved");
        }
        order.setStatus(PurchaseDocumentStatus.APPROVED);
        order.setApprovedAt(Instant.now());
        order.setApprovedBy(TenantContext.getActor());
        order.setUpdatedBy(TenantContext.getActor());
        return purchaseMapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public GoodsReceiptResponse receiveGoods(UUID organizationId, GoodsReceiptRequest request) {
        PurchaseOrder order = requireOrder(organizationId, request.getOrderId());
        if (order.getStatus() != PurchaseDocumentStatus.APPROVED
                && order.getStatus() != PurchaseDocumentStatus.PARTIALLY_RECEIVED) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order must be approved before receiving goods");
        }

        GoodsReceipt receipt = new GoodsReceipt();
        receipt.setOrganizationId(organizationId);
        receipt.setBranchId(order.getBranchId());
        receipt.setOrderId(order.getId());
        receipt.setReceiptNumber(documentNumberGenerator.next("GR"));
        receipt.setReceiptDate(request.getReceiptDate() != null ? request.getReceiptDate() : LocalDate.now());
        receipt.setNotes(request.getNotes());
        receipt.setCreatedBy(TenantContext.getActor());
        receipt.setUpdatedBy(TenantContext.getActor());
        receipt.setStatus(PurchaseDocumentStatus.DRAFT);
        GoodsReceipt savedReceipt = receiptRepository.save(receipt);

        int lineNum = 1;
        BigDecimal receivedValue = BigDecimal.ZERO;
        for (var lineReq : request.getLines()) {
            PurchaseOrderLine orderLine = order.getLines().stream()
                    .filter(l -> l.getId().equals(lineReq.getOrderLineId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Order line not found"));

            BigDecimal remaining = MoneyUtils.subtract(orderLine.getOrderedQuantity(), orderLine.getReceivedQuantity());
            if (lineReq.getReceivedQuantity().compareTo(remaining) > 0) {
                throw new BusinessException(ErrorCode.CONFLICT,
                        "Received quantity exceeds remaining for line " + orderLine.getLineNumber());
            }

            BigDecimal unitCost = lineReq.getUnitCost() != null ? lineReq.getUnitCost() : orderLine.getUnitPrice();
            var batch = inventoryService.receiveStock(ReceiveStockRequest.builder()
                    .organizationId(organizationId)
                    .branchId(order.getBranchId())
                    .productId(orderLine.getProductId())
                    .quantity(lineReq.getReceivedQuantity())
                    .unitCost(unitCost)
                    .batchNumber(lineReq.getBatchNumber())
                    .expiryDate(lineReq.getExpiryDate())
                    .sourceType(REF_GOODS_RECEIPT)
                    .sourceId(savedReceipt.getId())
                    .build());

            GoodsReceiptLine grLine = new GoodsReceiptLine();
            grLine.setReceipt(savedReceipt);
            grLine.setOrderLineId(orderLine.getId());
            grLine.setProductId(orderLine.getProductId());
            grLine.setLineNumber(lineNum++);
            grLine.setReceivedQuantity(lineReq.getReceivedQuantity());
            grLine.setUnitCost(unitCost);
            grLine.setBatchNumber(lineReq.getBatchNumber());
            grLine.setExpiryDate(lineReq.getExpiryDate());
            grLine.setStockBatchId(batch.getId());
            receipt.getLines().add(grLine);
            savedReceipt.getLines().add(grLine);

            orderLine.setReceivedQuantity(MoneyUtils.add(orderLine.getReceivedQuantity(), lineReq.getReceivedQuantity()));
            receivedValue = MoneyUtils.add(receivedValue, MoneyUtils.multiply(lineReq.getReceivedQuantity(), unitCost));
        }

        savedReceipt.setStatus(PurchaseDocumentStatus.RECEIVED);
        savedReceipt = receiptRepository.save(savedReceipt);
        updateOrderReceiveStatus(order, receivedValue);
        orderRepository.save(order);

        supplierService.recordTransaction(organizationId, order.getSupplierId(), SupplierTransactionType.PURCHASE,
                receivedValue, BigDecimal.ZERO, REF_GOODS_RECEIPT, savedReceipt.getId(),
                "Goods receipt " + savedReceipt.getReceiptNumber());

        return purchaseMapper.toReceiptResponse(savedReceipt);
    }

    @Transactional
    public SupplierPaymentResponse recordPayment(UUID organizationId, SupplierPaymentRequest request) {
        supplierService.requireSupplier(organizationId, request.getSupplierId());
        SupplierPayment payment = new SupplierPayment();
        payment.setOrganizationId(organizationId);
        payment.setSupplierId(request.getSupplierId());
        payment.setOrderId(request.getOrderId());
        payment.setPaymentNumber(documentNumberGenerator.next("SP"));
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now());
        payment.setAmount(MoneyUtils.scale(request.getAmount()));
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReference(request.getReference());
        payment.setNotes(request.getNotes());
        payment.setCreatedBy(TenantContext.getActor());
        payment.setUpdatedBy(TenantContext.getActor());
        SupplierPayment saved = paymentRepository.save(payment);

        supplierService.recordTransaction(organizationId, request.getSupplierId(), SupplierTransactionType.PAYMENT,
                BigDecimal.ZERO, saved.getAmount(), REF_SUPPLIER_PAYMENT, saved.getId(),
                "Payment " + saved.getPaymentNumber());

        return purchaseMapper.toPaymentResponse(saved);
    }

    @Transactional
    public PurchaseReturnResponse createReturn(UUID organizationId, PurchaseReturnRequest request) {
        supplierService.requireSupplier(organizationId, request.getSupplierId());
        PurchaseReturn purchaseReturn = new PurchaseReturn();
        purchaseReturn.setOrganizationId(organizationId);
        purchaseReturn.setBranchId(request.getBranchId());
        purchaseReturn.setSupplierId(request.getSupplierId());
        purchaseReturn.setOrderId(request.getOrderId());
        purchaseReturn.setReturnNumber(documentNumberGenerator.next("PR"));
        purchaseReturn.setStatus(PurchaseDocumentStatus.APPROVED);
        purchaseReturn.setReturnDate(request.getReturnDate() != null ? request.getReturnDate() : LocalDate.now());
        purchaseReturn.setReason(request.getReason());
        purchaseReturn.setNotes(request.getNotes());
        purchaseReturn.setCreatedBy(TenantContext.getActor());
        purchaseReturn.setUpdatedBy(TenantContext.getActor());

        int lineNum = 1;
        BigDecimal total = BigDecimal.ZERO;
        for (var lineReq : request.getLines()) {
            productLookupService.requireProduct(organizationId, lineReq.getProductId());
            if (lineReq.getStockBatchId() != null) {
                inventoryService.deductForPurchaseReturn(lineReq.getStockBatchId(), lineReq.getQuantity(),
                        REF_PURCHASE_RETURN, purchaseReturn.getId());
            }
            PurchaseReturnLine line = new PurchaseReturnLine();
            line.setPurchaseReturn(purchaseReturn);
            line.setProductId(lineReq.getProductId());
            line.setLineNumber(lineNum++);
            line.setQuantity(lineReq.getQuantity());
            line.setUnitPrice(lineReq.getUnitPrice());
            line.setLineTotal(MoneyUtils.multiply(lineReq.getQuantity(), lineReq.getUnitPrice()));
            line.setStockBatchId(lineReq.getStockBatchId());
            purchaseReturn.getLines().add(line);
            total = MoneyUtils.add(total, line.getLineTotal());
        }
        purchaseReturn.setTotalAmount(total);
        PurchaseReturn saved = returnRepository.save(purchaseReturn);

        supplierService.recordTransaction(organizationId, request.getSupplierId(), SupplierTransactionType.RETURN,
                BigDecimal.ZERO, total, REF_PURCHASE_RETURN, saved.getId(),
                "Purchase return " + saved.getReturnNumber());

        return purchaseMapper.toReturnResponse(saved);
    }

    @Transactional(readOnly = true)
    public PurchaseQuotationResponse getQuotation(UUID organizationId, UUID id) {
        return purchaseMapper.toQuotationResponse(requireQuotation(organizationId, id));
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getOrder(UUID organizationId, UUID id) {
        return purchaseMapper.toOrderResponse(requireOrder(organizationId, id));
    }

    private void updateOrderReceiveStatus(PurchaseOrder order, BigDecimal receivedValue) {
        order.setReceivedAmount(MoneyUtils.add(order.getReceivedAmount(), receivedValue));
        boolean fullyReceived = order.getLines().stream()
                .allMatch(l -> l.getReceivedQuantity().compareTo(l.getOrderedQuantity()) >= 0);
        order.setStatus(fullyReceived ? PurchaseDocumentStatus.RECEIVED : PurchaseDocumentStatus.PARTIALLY_RECEIVED);
        order.setUpdatedBy(TenantContext.getActor());
    }

    private PurchaseQuotation requireQuotation(UUID organizationId, UUID id) {
        return quotationRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Quotation not found"));
    }

    private PurchaseOrder requireOrder(UUID organizationId, UUID id) {
        return orderRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Purchase order not found"));
    }

    private PurchaseQuotationLine buildQuotationLine(PurchaseQuotation quotation, PurchaseLineRequest req, int lineNum) {
        PurchaseQuotationLine line = new PurchaseQuotationLine();
        line.setQuotation(quotation);
        line.setProductId(req.getProductId());
        line.setLineNumber(lineNum);
        line.setQuantity(req.getQuantity());
        line.setUnitPrice(req.getUnitPrice());
        line.setTaxRate(req.getTaxRate() != null ? req.getTaxRate() : BigDecimal.ZERO);
        line.setLineTotal(MoneyUtils.multiply(req.getQuantity(), req.getUnitPrice()));
        line.setNotes(req.getNotes());
        return line;
    }

    private PurchaseOrderLine buildOrderLine(PurchaseOrder order, PurchaseLineRequest req, int lineNum) {
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setOrder(order);
        line.setProductId(req.getProductId());
        line.setLineNumber(lineNum);
        line.setOrderedQuantity(req.getQuantity());
        line.setUnitPrice(req.getUnitPrice());
        line.setTaxRate(req.getTaxRate() != null ? req.getTaxRate() : BigDecimal.ZERO);
        line.setLineTotal(MoneyUtils.multiply(req.getQuantity(), req.getUnitPrice()));
        line.setNotes(req.getNotes());
        return line;
    }

    private BigDecimal computeTax(BigDecimal lineTotal, BigDecimal taxRate) {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return MoneyUtils.multiply(lineTotal, taxRate.divide(BigDecimal.valueOf(100)));
    }
}
