package com.supermarket.modules.customers.infrastructure;

import com.supermarket.modules.customers.domain.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {
}
