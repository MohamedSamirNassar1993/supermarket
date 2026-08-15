package com.supermarket.modules.customers.application.mapper;

import com.supermarket.modules.customers.application.dto.CustomerGroupResponse;
import com.supermarket.modules.customers.application.dto.CustomerResponse;
import com.supermarket.modules.customers.application.dto.CustomerStatementLineResponse;
import com.supermarket.modules.customers.application.dto.InstallmentResponse;
import com.supermarket.modules.customers.domain.Customer;
import com.supermarket.modules.customers.domain.CustomerGroup;
import com.supermarket.modules.customers.domain.CustomerTransaction;
import com.supermarket.modules.customers.domain.CustomerTransactionType;
import com.supermarket.modules.customers.domain.CustomerType;
import com.supermarket.modules.customers.domain.Installment;
import com.supermarket.modules.customers.domain.InstallmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "customerType", expression = "java(typeName(customer.getCustomerType()))")
    CustomerResponse toResponse(Customer customer);

    CustomerGroupResponse toGroupResponse(CustomerGroup group);

    @Mapping(target = "transactionType", expression = "java(txnTypeName(transaction.getTransactionType()))")
    CustomerStatementLineResponse toStatementLine(CustomerTransaction transaction);

    @Mapping(target = "status", expression = "java(statusName(installment.getStatus()))")
    InstallmentResponse toInstallmentResponse(Installment installment);

    default String typeName(CustomerType type) {
        return type != null ? type.name() : null;
    }

    default String txnTypeName(CustomerTransactionType type) {
        return type != null ? type.name() : null;
    }

    default String statusName(InstallmentStatus status) {
        return status != null ? status.name() : null;
    }
}
