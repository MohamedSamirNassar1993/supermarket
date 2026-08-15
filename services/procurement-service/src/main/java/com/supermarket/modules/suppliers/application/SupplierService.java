package com.supermarket.modules.suppliers.application;

import com.supermarket.modules.suppliers.application.dto.SupplierBalanceResponse;
import com.supermarket.modules.suppliers.application.dto.SupplierRatingRequest;
import com.supermarket.modules.suppliers.application.dto.SupplierRequest;
import com.supermarket.modules.suppliers.application.dto.SupplierResponse;
import com.supermarket.modules.suppliers.application.dto.SupplierStatementLineResponse;
import com.supermarket.modules.suppliers.application.mapper.SupplierMapper;
import com.supermarket.modules.suppliers.domain.Supplier;
import com.supermarket.modules.suppliers.domain.SupplierTransaction;
import com.supermarket.modules.suppliers.domain.SupplierTransactionType;
import com.supermarket.modules.suppliers.infrastructure.SupplierRepository;
import com.supermarket.modules.suppliers.infrastructure.SupplierTransactionRepository;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.api.PageResponse;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.MoneyUtils;
import com.supermarket.shared.domain.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierTransactionRepository transactionRepository;
    private final SupplierMapper supplierMapper;

    @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> list(UUID organizationId, Pageable pageable) {
        Page<Supplier> page = supplierRepository.findByOrganizationId(organizationId, pageable);
        return PageResponse.from(page, supplierMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SupplierResponse get(UUID organizationId, UUID id) {
        return supplierMapper.toResponse(requireSupplier(organizationId, id));
    }

    @Transactional
    public SupplierResponse create(UUID organizationId, SupplierRequest request) {
        supplierRepository.findByOrganizationIdAndCode(organizationId, request.getCode())
                .ifPresent(s -> {
                    throw new BusinessException(ErrorCode.CONFLICT, "Supplier code already exists");
                });

        Supplier supplier = new Supplier();
        supplier.setOrganizationId(organizationId);
        applyRequest(supplier, request);
        supplier.setActive(request.getActive() == null || request.getActive());
        supplier.setCreatedBy(TenantContext.getActor());
        supplier.setUpdatedBy(TenantContext.getActor());
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(UUID organizationId, UUID id, SupplierRequest request) {
        Supplier supplier = requireSupplier(organizationId, id);
        if (!supplier.getCode().equals(request.getCode())) {
            supplierRepository.findByOrganizationIdAndCode(organizationId, request.getCode())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new BusinessException(ErrorCode.CONFLICT, "Supplier code already exists");
                        }
                    });
        }
        applyRequest(supplier, request);
        if (request.getActive() != null) {
            supplier.setActive(request.getActive());
        }
        supplier.setUpdatedBy(TenantContext.getActor());
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Supplier supplier = requireSupplier(organizationId, id);
        supplier.setActive(false);
        supplier.setUpdatedBy(TenantContext.getActor());
        supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public SupplierBalanceResponse getBalance(UUID organizationId, UUID id) {
        Supplier supplier = requireSupplier(organizationId, id);
        BigDecimal balance = transactionRepository.findLatestBalance(id).orElse(BigDecimal.ZERO);
        BigDecimal available = MoneyUtils.subtract(supplier.getCreditLimit(), balance);
        return SupplierBalanceResponse.builder()
                .supplierId(id)
                .balance(balance)
                .creditLimit(supplier.getCreditLimit())
                .availableCredit(available.max(BigDecimal.ZERO))
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplierStatementLineResponse> getStatement(UUID organizationId, UUID id, Pageable pageable) {
        requireSupplier(organizationId, id);
        Page<SupplierTransaction> page = transactionRepository
                .findBySupplierIdOrderByTransactionDateDescCreatedAtDesc(id, pageable);
        return PageResponse.from(page, supplierMapper::toStatementLine);
    }

    @Transactional
    public SupplierResponse updateRating(UUID organizationId, UUID id, SupplierRatingRequest request) {
        Supplier supplier = requireSupplier(organizationId, id);
        supplier.setRating(MoneyUtils.scale(request.getRating()));
        supplier.setUpdatedBy(TenantContext.getActor());
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierTransaction recordTransaction(UUID organizationId, UUID supplierId,
                                                  SupplierTransactionType type, BigDecimal debit, BigDecimal credit,
                                                  String referenceType, UUID referenceId, String description) {
        requireSupplier(organizationId, supplierId);
        BigDecimal currentBalance = transactionRepository.findLatestBalance(supplierId).orElse(BigDecimal.ZERO);
        BigDecimal newBalance = MoneyUtils.add(MoneyUtils.subtract(currentBalance, credit), debit);

        SupplierTransaction txn = new SupplierTransaction();
        txn.setOrganizationId(organizationId);
        txn.setSupplierId(supplierId);
        txn.setTransactionType(type);
        txn.setDebit(MoneyUtils.scale(debit));
        txn.setCredit(MoneyUtils.scale(credit));
        txn.setBalanceAfter(newBalance);
        txn.setReferenceType(referenceType);
        txn.setReferenceId(referenceId);
        txn.setDescription(description);
        txn.setTransactionDate(LocalDate.now());
        txn.setCreatedBy(TenantContext.getActor());
        txn.setUpdatedBy(TenantContext.getActor());
        return transactionRepository.save(txn);
    }

    public Supplier requireSupplier(UUID organizationId, UUID id) {
        return supplierRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Supplier not found"));
    }

    private void applyRequest(Supplier supplier, SupplierRequest request) {
        supplier.setCode(request.getCode().trim().toUpperCase());
        supplier.setName(request.getName().trim());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setTaxId(request.getTaxId());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setCreditLimit(MoneyUtils.scale(request.getCreditLimit()));
        supplier.setNotes(request.getNotes());
    }
}
