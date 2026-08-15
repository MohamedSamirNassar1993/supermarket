package com.supermarket.modules.customers.application;

import com.supermarket.modules.customers.application.dto.CustomerGroupRequest;
import com.supermarket.modules.customers.application.dto.CustomerGroupResponse;
import com.supermarket.modules.customers.application.dto.CustomerRequest;
import com.supermarket.modules.customers.application.dto.CustomerResponse;
import com.supermarket.modules.customers.application.dto.CustomerStatementLineResponse;
import com.supermarket.modules.customers.application.dto.InstallmentPlanRequest;
import com.supermarket.modules.customers.application.dto.InstallmentPlanResponse;
import com.supermarket.modules.customers.application.dto.InstallmentResponse;
import com.supermarket.modules.customers.application.dto.LoyaltyAdjustRequest;
import com.supermarket.modules.customers.application.mapper.CustomerMapper;
import com.supermarket.modules.customers.domain.Customer;
import com.supermarket.modules.customers.domain.CustomerGroup;
import com.supermarket.modules.customers.domain.CustomerTransaction;
import com.supermarket.modules.customers.domain.CustomerTransactionType;
import com.supermarket.modules.customers.domain.Installment;
import com.supermarket.modules.customers.domain.InstallmentStatus;
import com.supermarket.modules.customers.domain.LoyaltyTransaction;
import com.supermarket.modules.customers.infrastructure.CustomerGroupRepository;
import com.supermarket.modules.customers.infrastructure.CustomerRepository;
import com.supermarket.modules.customers.infrastructure.CustomerTransactionRepository;
import com.supermarket.modules.customers.infrastructure.InstallmentRepository;
import com.supermarket.modules.customers.infrastructure.LoyaltyTransactionRepository;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerGroupRepository groupRepository;
    private final CustomerTransactionRepository transactionRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final InstallmentRepository installmentRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> list(UUID organizationId, Pageable pageable) {
        Page<Customer> page = customerRepository.findByOrganizationId(organizationId, pageable);
        return PageResponse.from(page, customerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(UUID organizationId, UUID id) {
        return customerMapper.toResponse(requireCustomer(organizationId, id));
    }

    @Transactional
    public CustomerResponse create(UUID organizationId, CustomerRequest request) {
        customerRepository.findByOrganizationIdAndCode(organizationId, request.getCode())
                .ifPresent(c -> {
                    throw new BusinessException(ErrorCode.CONFLICT, "Customer code already exists");
                });
        if (request.getGroupId() != null) {
            requireGroup(organizationId, request.getGroupId());
        }
        Customer customer = new Customer();
        customer.setOrganizationId(organizationId);
        applyRequest(customer, request);
        customer.setActive(request.getActive() == null || request.getActive());
        customer.setCreatedBy(TenantContext.getActor());
        customer.setUpdatedBy(TenantContext.getActor());
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(UUID organizationId, UUID id, CustomerRequest request) {
        Customer customer = requireCustomer(organizationId, id);
        if (!customer.getCode().equals(request.getCode())) {
            customerRepository.findByOrganizationIdAndCode(organizationId, request.getCode())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new BusinessException(ErrorCode.CONFLICT, "Customer code already exists");
                        }
                    });
        }
        if (request.getGroupId() != null) {
            requireGroup(organizationId, request.getGroupId());
        }
        applyRequest(customer, request);
        if (request.getActive() != null) {
            customer.setActive(request.getActive());
        }
        customer.setUpdatedBy(TenantContext.getActor());
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Customer customer = requireCustomer(organizationId, id);
        customer.setActive(false);
        customer.setUpdatedBy(TenantContext.getActor());
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerStatementLineResponse> getStatement(UUID organizationId, UUID id, Pageable pageable) {
        requireCustomer(organizationId, id);
        Page<CustomerTransaction> page = transactionRepository
                .findByCustomerIdOrderByTransactionDateDescCreatedAtDesc(id, pageable);
        return PageResponse.from(page, customerMapper::toStatementLine);
    }

    @Transactional
    public CustomerResponse adjustLoyalty(UUID organizationId, UUID id, LoyaltyAdjustRequest request) {
        Customer customer = requireCustomer(organizationId, id);
        long newBalance = customer.getLoyaltyPoints() + request.getPoints();
        if (newBalance < 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Insufficient loyalty points");
        }
        customer.setLoyaltyPoints(newBalance);
        customer.setUpdatedBy(TenantContext.getActor());
        customerRepository.save(customer);

        LoyaltyTransaction txn = new LoyaltyTransaction();
        txn.setOrganizationId(organizationId);
        txn.setCustomerId(id);
        txn.setTransactionType(request.getPoints() >= 0 ? "EARN" : "REDEEM");
        txn.setPoints(request.getPoints());
        txn.setBalanceAfter(newBalance);
        txn.setDescription(request.getDescription());
        txn.setTransactionDate(LocalDate.now());
        txn.setCreatedBy(TenantContext.getActor());
        txn.setUpdatedBy(TenantContext.getActor());
        loyaltyTransactionRepository.save(txn);

        return customerMapper.toResponse(customer);
    }

    @Transactional
    public InstallmentPlanResponse createInstallmentPlan(UUID organizationId, UUID customerId,
                                                          InstallmentPlanRequest request) {
        requireCustomer(organizationId, customerId);
        BigDecimal installmentAmount = request.getTotalAmount()
                .divide(BigDecimal.valueOf(request.getNumberOfInstallments()), 4, RoundingMode.HALF_UP);

        List<Installment> installments = new ArrayList<>();
        List<InstallmentResponse> responses = new ArrayList<>();
        LocalDate dueDate = request.getFirstDueDate();

        for (int i = 1; i <= request.getNumberOfInstallments(); i++) {
            Installment installment = new Installment();
            installment.setOrganizationId(organizationId);
            installment.setCustomerId(customerId);
            installment.setReferenceType(request.getReferenceType());
            installment.setReferenceId(request.getReferenceId());
            installment.setInstallmentNumber(i);
            installment.setDueDate(dueDate);
            installment.setAmount(MoneyUtils.scale(installmentAmount));
            installment.setStatus(InstallmentStatus.PENDING);
            installment.setCreatedBy(TenantContext.getActor());
            installment.setUpdatedBy(TenantContext.getActor());
            installments.add(installmentRepository.save(installment));
            responses.add(customerMapper.toInstallmentResponse(installment));
            dueDate = dueDate.plusMonths(1);
        }

        return InstallmentPlanResponse.builder()
                .customerId(customerId)
                .installments(responses)
                .build();
    }

    @Transactional
    public List<CustomerGroupResponse> listGroups(UUID organizationId) {
        return groupRepository.findByOrganizationId(organizationId).stream()
                .map(customerMapper::toGroupResponse)
                .toList();
    }

    @Transactional
    public CustomerGroupResponse createGroup(UUID organizationId, CustomerGroupRequest request) {
        CustomerGroup group = new CustomerGroup();
        group.setOrganizationId(organizationId);
        group.setCode(request.getCode().trim().toUpperCase());
        group.setName(request.getName());
        group.setDiscountRate(MoneyUtils.scale(request.getDiscountRate()));
        group.setDescription(request.getDescription());
        group.setActive(request.getActive() == null || request.getActive());
        group.setCreatedBy(TenantContext.getActor());
        group.setUpdatedBy(TenantContext.getActor());
        return customerMapper.toGroupResponse(groupRepository.save(group));
    }

    @Transactional
    public CustomerTransaction recordTransaction(UUID organizationId, UUID customerId,
                                                  CustomerTransactionType type, BigDecimal debit, BigDecimal credit,
                                                  String referenceType, UUID referenceId, String description) {
        Customer customer = requireCustomer(organizationId, customerId);
        BigDecimal newBalance = MoneyUtils.add(MoneyUtils.subtract(customer.getCurrentBalance(), credit), debit);

        if (type == CustomerTransactionType.SALE && customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0) {
            if (newBalance.compareTo(customer.getCreditLimit()) > 0) {
                throw new BusinessException(ErrorCode.CONFLICT, "Credit limit exceeded for customer");
            }
        }

        customer.setCurrentBalance(newBalance);
        customer.setUpdatedBy(TenantContext.getActor());
        customerRepository.save(customer);

        CustomerTransaction txn = new CustomerTransaction();
        txn.setOrganizationId(organizationId);
        txn.setCustomerId(customerId);
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

    @Transactional
    public void earnLoyaltyPoints(UUID organizationId, UUID customerId, long points,
                                   String referenceType, UUID referenceId, String description) {
        if (points <= 0) {
            return;
        }
        Customer customer = requireCustomer(organizationId, customerId);
        long newBalance = customer.getLoyaltyPoints() + points;
        customer.setLoyaltyPoints(newBalance);
        customer.setUpdatedBy(TenantContext.getActor());
        customerRepository.save(customer);

        LoyaltyTransaction txn = new LoyaltyTransaction();
        txn.setOrganizationId(organizationId);
        txn.setCustomerId(customerId);
        txn.setTransactionType("EARN");
        txn.setPoints(points);
        txn.setBalanceAfter(newBalance);
        txn.setReferenceType(referenceType);
        txn.setReferenceId(referenceId);
        txn.setDescription(description);
        txn.setTransactionDate(LocalDate.now());
        txn.setCreatedBy(TenantContext.getActor());
        txn.setUpdatedBy(TenantContext.getActor());
        loyaltyTransactionRepository.save(txn);
    }

    @Transactional
    public void redeemLoyaltyPoints(UUID organizationId, UUID customerId, long points,
                                     String referenceType, UUID referenceId, String description) {
        Customer customer = requireCustomer(organizationId, customerId);
        if (customer.getLoyaltyPoints() < points) {
            throw new BusinessException(ErrorCode.CONFLICT, "Insufficient loyalty points");
        }
        long newBalance = customer.getLoyaltyPoints() - points;
        customer.setLoyaltyPoints(newBalance);
        customer.setUpdatedBy(TenantContext.getActor());
        customerRepository.save(customer);

        LoyaltyTransaction txn = new LoyaltyTransaction();
        txn.setOrganizationId(organizationId);
        txn.setCustomerId(customerId);
        txn.setTransactionType("REDEEM");
        txn.setPoints(-points);
        txn.setBalanceAfter(newBalance);
        txn.setReferenceType(referenceType);
        txn.setReferenceId(referenceId);
        txn.setDescription(description);
        txn.setTransactionDate(LocalDate.now());
        txn.setCreatedBy(TenantContext.getActor());
        txn.setUpdatedBy(TenantContext.getActor());
        loyaltyTransactionRepository.save(txn);
    }

    public Customer requireCustomer(UUID organizationId, UUID id) {
        return customerRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Customer not found"));
    }

    private CustomerGroup requireGroup(UUID organizationId, UUID id) {
        return groupRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Customer group not found"));
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setGroupId(request.getGroupId());
        customer.setCode(request.getCode().trim().toUpperCase());
        customer.setName(request.getName().trim());
        customer.setCustomerType(request.getCustomerType());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setTaxId(request.getTaxId());
        customer.setCreditLimit(MoneyUtils.scale(request.getCreditLimit()));
        customer.setNotes(request.getNotes());
    }
}
