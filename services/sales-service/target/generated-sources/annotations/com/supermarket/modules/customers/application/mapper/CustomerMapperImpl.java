package com.supermarket.modules.customers.application.mapper;

import com.supermarket.modules.customers.application.dto.CustomerGroupResponse;
import com.supermarket.modules.customers.application.dto.CustomerResponse;
import com.supermarket.modules.customers.application.dto.CustomerStatementLineResponse;
import com.supermarket.modules.customers.application.dto.InstallmentResponse;
import com.supermarket.modules.customers.domain.Customer;
import com.supermarket.modules.customers.domain.CustomerGroup;
import com.supermarket.modules.customers.domain.CustomerTransaction;
import com.supermarket.modules.customers.domain.Installment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-11T02:21:29+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerResponse toResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerResponse.CustomerResponseBuilder customerResponse = CustomerResponse.builder();

        customerResponse.id( customer.getId() );
        customerResponse.groupId( customer.getGroupId() );
        customerResponse.code( customer.getCode() );
        customerResponse.name( customer.getName() );
        customerResponse.email( customer.getEmail() );
        customerResponse.phone( customer.getPhone() );
        customerResponse.address( customer.getAddress() );
        customerResponse.taxId( customer.getTaxId() );
        customerResponse.creditLimit( customer.getCreditLimit() );
        customerResponse.currentBalance( customer.getCurrentBalance() );
        customerResponse.loyaltyPoints( customer.getLoyaltyPoints() );
        customerResponse.active( customer.isActive() );
        customerResponse.notes( customer.getNotes() );
        customerResponse.createdAt( customer.getCreatedAt() );

        customerResponse.customerType( typeName(customer.getCustomerType()) );

        return customerResponse.build();
    }

    @Override
    public CustomerGroupResponse toGroupResponse(CustomerGroup group) {
        if ( group == null ) {
            return null;
        }

        CustomerGroupResponse.CustomerGroupResponseBuilder customerGroupResponse = CustomerGroupResponse.builder();

        customerGroupResponse.id( group.getId() );
        customerGroupResponse.code( group.getCode() );
        customerGroupResponse.name( group.getName() );
        customerGroupResponse.discountRate( group.getDiscountRate() );
        customerGroupResponse.description( group.getDescription() );
        customerGroupResponse.active( group.isActive() );

        return customerGroupResponse.build();
    }

    @Override
    public CustomerStatementLineResponse toStatementLine(CustomerTransaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        CustomerStatementLineResponse.CustomerStatementLineResponseBuilder customerStatementLineResponse = CustomerStatementLineResponse.builder();

        customerStatementLineResponse.id( transaction.getId() );
        customerStatementLineResponse.debit( transaction.getDebit() );
        customerStatementLineResponse.credit( transaction.getCredit() );
        customerStatementLineResponse.balanceAfter( transaction.getBalanceAfter() );
        customerStatementLineResponse.description( transaction.getDescription() );
        customerStatementLineResponse.transactionDate( transaction.getTransactionDate() );

        customerStatementLineResponse.transactionType( txnTypeName(transaction.getTransactionType()) );

        return customerStatementLineResponse.build();
    }

    @Override
    public InstallmentResponse toInstallmentResponse(Installment installment) {
        if ( installment == null ) {
            return null;
        }

        InstallmentResponse.InstallmentResponseBuilder installmentResponse = InstallmentResponse.builder();

        installmentResponse.id( installment.getId() );
        installmentResponse.installmentNumber( installment.getInstallmentNumber() );
        installmentResponse.dueDate( installment.getDueDate() );
        installmentResponse.amount( installment.getAmount() );
        installmentResponse.paidAmount( installment.getPaidAmount() );

        installmentResponse.status( statusName(installment.getStatus()) );

        return installmentResponse.build();
    }
}
