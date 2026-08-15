package com.supermarket.modules.suppliers.application.mapper;

import com.supermarket.modules.suppliers.application.dto.SupplierResponse;
import com.supermarket.modules.suppliers.application.dto.SupplierStatementLineResponse;
import com.supermarket.modules.suppliers.domain.Supplier;
import com.supermarket.modules.suppliers.domain.SupplierTransaction;
import com.supermarket.modules.suppliers.domain.SupplierTransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SupplierMapper {

    SupplierResponse toResponse(Supplier supplier);

    SupplierStatementLineResponse toStatementLine(SupplierTransaction transaction);

    default String map(SupplierTransactionType type) {
        return type != null ? type.name() : null;
    }
}
