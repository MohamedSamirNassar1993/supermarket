package com.supermarket.modules.sales.infrastructure;

import com.supermarket.modules.sales.domain.PosSyncQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PosSyncQueueRepository extends JpaRepository<PosSyncQueue, UUID> {

    List<PosSyncQueue> findByStatusOrderByCreatedAtAsc(String status);
}
