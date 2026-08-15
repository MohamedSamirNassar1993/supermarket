package com.supermarket.modules.suppliers.application.mapper;

import com.supermarket.modules.suppliers.application.dto.SupplierResponse;
import com.supermarket.modules.suppliers.application.dto.SupplierStatementLineResponse;
import com.supermarket.modules.suppliers.domain.Supplier;
import com.supermarket.modules.suppliers.domain.SupplierTransaction;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-11T02:21:19+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public SupplierResponse toResponse(Supplier supplier) {
        if ( supplier == null ) {
            return null;
        }

        SupplierResponse.SupplierResponseBuilder supplierResponse = SupplierResponse.builder();

        supplierResponse.id( supplier.getId() );
        supplierResponse.code( supplier.getCode() );
        supplierResponse.name( supplier.getName() );
        supplierResponse.contactPerson( supplier.getContactPerson() );
        supplierResponse.email( supplier.getEmail() );
        supplierResponse.phone( supplier.getPhone() );
        supplierResponse.address( supplier.getAddress() );
        supplierResponse.taxId( supplier.getTaxId() );
        supplierResponse.paymentTerms( supplier.getPaymentTerms() );
        supplierResponse.creditLimit( supplier.getCreditLimit() );
        supplierResponse.rating( supplier.getRating() );
        supplierResponse.active( supplier.isActive() );
        supplierResponse.notes( supplier.getNotes() );
        supplierResponse.createdAt( supplier.getCreatedAt() );
        supplierResponse.updatedAt( supplier.getUpdatedAt() );

        return supplierResponse.build();
    }

    @Override
    public SupplierStatementLineResponse toStatementLine(SupplierTransaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        SupplierStatementLineResponse.SupplierStatementLineResponseBuilder supplierStatementLineResponse = SupplierStatementLineResponse.builder();

        supplierStatementLineResponse.id( transaction.getId() );
        supplierStatementLineResponse.transactionType( map( transaction.getTransactionType() ) );
        supplierStatementLineResponse.referenceType( transaction.getReferenceType() );
        supplierStatementLineResponse.referenceId( transaction.getReferenceId() );
        supplierStatementLineResponse.debit( transaction.getDebit() );
        supplierStatementLineResponse.credit( transaction.getCredit() );
        supplierStatementLineResponse.balanceAfter( transaction.getBalanceAfter() );
        supplierStatementLineResponse.description( transaction.getDescription() );
        supplierStatementLineResponse.transactionDate( transaction.getTransactionDate() );

        return supplierStatementLineResponse.build();
    }
}
