package com.supermarket.modules.sales.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.modules.sales.application.dto.CreateSalesInvoiceRequest;
import com.supermarket.modules.sales.application.dto.PosSyncRequest;
import com.supermarket.modules.sales.application.dto.PosSyncResponse;
import com.supermarket.modules.sales.application.dto.SalesInvoiceResponse;
import com.supermarket.modules.sales.application.dto.SalesLineRequest;
import com.supermarket.modules.sales.application.dto.SalesPaymentRequest;
import com.supermarket.modules.sales.application.dto.SalesReturnRequest;
import com.supermarket.modules.sales.application.dto.SalesReturnResponse;
import com.supermarket.modules.sales.application.mapper.SalesMapper;
import com.supermarket.modules.sales.domain.PosSyncQueue;
import com.supermarket.modules.sales.domain.SaleStatus;
import com.supermarket.modules.sales.domain.SalesInvoice;
import com.supermarket.modules.sales.domain.SalesLine;
import com.supermarket.modules.sales.domain.SalesLineBatch;
import com.supermarket.modules.sales.domain.SalesPayment;
import com.supermarket.modules.sales.domain.SalesReturn;
import com.supermarket.modules.sales.domain.SalesReturnLine;
import com.supermarket.modules.sales.infrastructure.PosSyncQueueRepository;
import com.supermarket.modules.sales.infrastructure.SalesInvoiceRepository;
import com.supermarket.modules.sales.infrastructure.SalesReturnRepository;
import com.supermarket.modules.customers.application.CustomerService;
import com.supermarket.modules.customers.domain.CustomerTransactionType;
import com.supermarket.modules.inventory.application.InventoryService;
import com.supermarket.modules.inventory.application.dto.AllocateStockRequest;
import com.supermarket.modules.inventory.application.dto.BatchAllocation;
import com.supermarket.modules.products.application.ProductLookupService;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesService {

    private static final String REF_SALES_INVOICE = "SALES_INVOICE";
    private static final String REF_SALES_RETURN = "SALES_RETURN";

    private final SalesInvoiceRepository invoiceRepository;
    private final SalesReturnRepository returnRepository;
    private final PosSyncQueueRepository posSyncQueueRepository;
    private final SalesMapper salesMapper;
    private final ProductLookupService productLookupService;
    private final InventoryService inventoryService;
    private final CustomerService customerService;
    private final PromotionEngine promotionEngine;
    private final DocumentNumberGenerator documentNumberGenerator;
    private final ObjectMapper objectMapper;

    @Transactional
    public SalesInvoiceResponse createInvoice(UUID organizationId, CreateSalesInvoiceRequest request) {
        if (request.getCustomerId() != null) {
            customerService.requireCustomer(organizationId, request.getCustomerId());
        }

        SalesInvoice invoice = new SalesInvoice();
        invoice.setOrganizationId(organizationId);
        invoice.setBranchId(request.getBranchId());
        invoice.setCustomerId(request.getCustomerId());
        invoice.setInvoiceNumber(documentNumberGenerator.next("SI"));
        invoice.setSaleType(request.getSaleType());
        invoice.setStatus(SaleStatus.DRAFT);
        invoice.setPosTerminalId(request.getPosTerminalId());
        invoice.setNotes(request.getNotes());
        invoice.setCreatedBy(TenantContext.getActor());
        invoice.setUpdatedBy(TenantContext.getActor());

        int lineNum = 1;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        List<SalesLine> lines = new ArrayList<>();

        for (SalesLineRequest lineReq : request.getLines()) {
            ProductEntity product = productLookupService.requireProduct(organizationId, lineReq.getProductId());
            BigDecimal unitPrice = lineReq.getUnitPrice() != null ? lineReq.getUnitPrice() : product.getBasePrice();
            BigDecimal taxRate = lineReq.getTaxRate() != null ? lineReq.getTaxRate() : product.getTaxRate();

            SalesLine line = new SalesLine();
            line.setInvoice(invoice);
            line.setProductId(lineReq.getProductId());
            line.setLineNumber(lineNum++);
            line.setQuantity(lineReq.getQuantity());
            line.setUnitPrice(MoneyUtils.scale(unitPrice));
            line.setTaxRate(taxRate);
            BigDecimal lineSubtotal = MoneyUtils.multiply(unitPrice, lineReq.getQuantity());
            line.setLineTotal(lineSubtotal);
            lines.add(line);
            subtotal = MoneyUtils.add(subtotal, lineSubtotal);
            taxTotal = MoneyUtils.add(taxTotal, computeTax(lineSubtotal, taxRate));
        }
        invoice.getLines().addAll(lines);
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxTotal);

        var promoResult = promotionEngine.applyPromotions(organizationId, lines, subtotal);
        BigDecimal discount = MoneyUtils.scale(promoResult.getTotalDiscount());
        invoice.setDiscountAmount(discount);
        invoice.setTotalAmount(MoneyUtils.subtract(MoneyUtils.add(subtotal, taxTotal), discount));

        if (promoResult.getAppliedPromotionIds() != null && !promoResult.getAppliedPromotionIds().isEmpty()) {
            invoice.setPromotionIds(promoResult.getAppliedPromotionIds().toArray(new UUID[0]));
        }

        SalesInvoice savedDraft = invoiceRepository.save(invoice);

        if (request.getLoyaltyPointsRedeemed() != null && request.getLoyaltyPointsRedeemed() > 0
                && request.getCustomerId() != null) {
            savedDraft.setLoyaltyPointsRedeemed(request.getLoyaltyPointsRedeemed());
            customerService.redeemLoyaltyPoints(organizationId, request.getCustomerId(),
                    request.getLoyaltyPointsRedeemed(), REF_SALES_INVOICE, savedDraft.getId(),
                    "Redeemed on invoice " + savedDraft.getInvoiceNumber());
        }

        for (SalesLine line : savedDraft.getLines()) {
            List<BatchAllocation> allocations = inventoryService.allocateForSale(
                    AllocateStockRequest.builder()
                            .organizationId(organizationId)
                            .branchId(request.getBranchId())
                            .productId(line.getProductId())
                            .quantity(line.getQuantity())
                            .referenceType(REF_SALES_INVOICE)
                            .referenceId(savedDraft.getId())
                            .build());
            for (BatchAllocation allocation : allocations) {
                SalesLineBatch batch = new SalesLineBatch();
                batch.setSalesLine(line);
                batch.setStockBatchId(allocation.getBatchId());
                batch.setQuantity(allocation.getQuantity());
                line.getBatches().add(batch);
            }
        }

        BigDecimal paidTotal = BigDecimal.ZERO;
        if (request.getPayments() != null) {
            for (SalesPaymentRequest payReq : request.getPayments()) {
                SalesPayment payment = new SalesPayment();
                payment.setInvoice(savedDraft);
                payment.setPaymentMethod(payReq.getPaymentMethod());
                payment.setAmount(MoneyUtils.scale(payReq.getAmount()));
                payment.setReference(payReq.getReference());
                payment.setPaidAt(Instant.now());
                savedDraft.getPayments().add(payment);
                paidTotal = MoneyUtils.add(paidTotal, payReq.getAmount());
            }
        }
        savedDraft.setPaidAmount(paidTotal);

        if (paidTotal.compareTo(savedDraft.getTotalAmount()) >= 0) {
            savedDraft.setStatus(SaleStatus.PAID);
        } else if (paidTotal.compareTo(BigDecimal.ZERO) > 0) {
            savedDraft.setStatus(SaleStatus.PARTIALLY_PAID);
        } else {
            savedDraft.setStatus(SaleStatus.CONFIRMED);
        }

        if (request.getCustomerId() != null) {
            BigDecimal creditAmount = MoneyUtils.subtract(savedDraft.getTotalAmount(), paidTotal);
            if (creditAmount.compareTo(BigDecimal.ZERO) > 0) {
                customerService.recordTransaction(organizationId, request.getCustomerId(),
                        CustomerTransactionType.SALE, creditAmount, BigDecimal.ZERO,
                        REF_SALES_INVOICE, savedDraft.getId(), "Sale on credit " + savedDraft.getInvoiceNumber());
            }
            long pointsEarned = savedDraft.getTotalAmount().longValue();
            savedDraft.setLoyaltyPointsEarned(pointsEarned);
            customerService.earnLoyaltyPoints(organizationId, request.getCustomerId(), pointsEarned,
                    REF_SALES_INVOICE, savedDraft.getId(), "Points earned on " + savedDraft.getInvoiceNumber());
        }

        savedDraft.setUpdatedBy(TenantContext.getActor());
        SalesInvoice saved = invoiceRepository.save(savedDraft);
        return salesMapper.toInvoiceResponse(saved);
    }

    @Transactional(readOnly = true)
    public SalesInvoiceResponse getInvoice(UUID organizationId, UUID id) {
        return salesMapper.toInvoiceResponse(requireInvoice(organizationId, id));
    }

    @Transactional
    public SalesReturnResponse createReturn(UUID organizationId, SalesReturnRequest request) {
        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setOrganizationId(organizationId);
        salesReturn.setBranchId(request.getBranchId());
        salesReturn.setInvoiceId(request.getInvoiceId());
        salesReturn.setCustomerId(request.getCustomerId());
        salesReturn.setReturnNumber(documentNumberGenerator.next("SR"));
        salesReturn.setStatus(SaleStatus.CONFIRMED);
        salesReturn.setRefundMethod(request.getRefundMethod());
        salesReturn.setReason(request.getReason());
        salesReturn.setNotes(request.getNotes());
        salesReturn.setCreatedBy(TenantContext.getActor());
        salesReturn.setUpdatedBy(TenantContext.getActor());

        int lineNum = 1;
        BigDecimal refundTotal = BigDecimal.ZERO;
        for (var lineReq : request.getLines()) {
            productLookupService.requireProduct(organizationId, lineReq.getProductId());
            inventoryService.returnToStock(organizationId, request.getBranchId(), lineReq.getProductId(),
                    lineReq.getStockBatchId(), lineReq.getQuantity(), REF_SALES_RETURN, salesReturn.getId());

            SalesReturnLine line = new SalesReturnLine();
            line.setSalesReturn(salesReturn);
            line.setProductId(lineReq.getProductId());
            line.setSalesLineId(lineReq.getSalesLineId());
            line.setLineNumber(lineNum++);
            line.setQuantity(lineReq.getQuantity());
            line.setUnitPrice(lineReq.getUnitPrice());
            line.setLineTotal(MoneyUtils.multiply(lineReq.getQuantity(), lineReq.getUnitPrice()));
            line.setStockBatchId(lineReq.getStockBatchId());
            salesReturn.getLines().add(line);
            refundTotal = MoneyUtils.add(refundTotal, line.getLineTotal());
        }
        salesReturn.setRefundAmount(refundTotal);
        SalesReturn saved = returnRepository.save(salesReturn);

        if (request.getCustomerId() != null) {
            customerService.recordTransaction(organizationId, request.getCustomerId(),
                    CustomerTransactionType.RETURN, BigDecimal.ZERO, refundTotal,
                    REF_SALES_RETURN, saved.getId(), "Sales return " + saved.getReturnNumber());
        }

        return salesMapper.toReturnResponse(saved);
    }

    @Transactional
    public PosSyncResponse processPosSync(UUID organizationId, PosSyncRequest request) {
        PosSyncQueue entry = new PosSyncQueue();
        entry.setOrganizationId(organizationId);
        entry.setBranchId(request.getBranchId());
        entry.setTerminalId(request.getTerminalId());
        entry.setPayloadType(request.getPayloadType());
        entry.setPayload(request.getPayload());
        entry.setStatus("PENDING");
        entry = posSyncQueueRepository.save(entry);

        try {
            UUID invoiceId = null;
            if ("SALE".equalsIgnoreCase(request.getPayloadType())) {
                CreateSalesInvoiceRequest invoiceRequest = objectMapper.convertValue(
                        request.getPayload(), CreateSalesInvoiceRequest.class);
                invoiceRequest.setBranchId(request.getBranchId());
                invoiceRequest.setPosTerminalId(request.getTerminalId());
                SalesInvoiceResponse invoice = createInvoice(organizationId, invoiceRequest);
                invoiceId = invoice.getId();
            }
            entry.setStatus("PROCESSED");
            entry.setProcessedAt(Instant.now());
            posSyncQueueRepository.save(entry);
            return PosSyncResponse.builder()
                    .id(entry.getId())
                    .status("PROCESSED")
                    .invoiceId(invoiceId)
                    .build();
        } catch (Exception ex) {
            entry.setStatus("FAILED");
            entry.setErrorMessage(ex.getMessage());
            entry.setProcessedAt(Instant.now());
            posSyncQueueRepository.save(entry);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "POS sync failed: " + ex.getMessage());
        }
    }

    private SalesInvoice requireInvoice(UUID organizationId, UUID id) {
        return invoiceRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Sales invoice not found"));
    }

    private BigDecimal computeTax(BigDecimal lineTotal, BigDecimal taxRate) {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return MoneyUtils.multiply(lineTotal, taxRate.divide(BigDecimal.valueOf(100)));
    }
}
