package com.supermarket.modules.losses.infrastructure.persistence;

import com.supermarket.modules.losses.domain.LossRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LossRecordJpaRepository extends JpaRepository<LossRecord, UUID> {
    Page<LossRecord> findByBranchId(UUID branchId, Pageable pageable);
}
