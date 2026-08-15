package com.supermarket.modules.losses.application;

import com.supermarket.modules.losses.domain.LossRecord;
import com.supermarket.modules.losses.infrastructure.persistence.LossRecordJpaRepository;
import com.supermarket.shared.audit.AuditAction;
import com.supermarket.shared.audit.Audited;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LossRecordService {

    private final LossRecordJpaRepository lossRecordRepository;

    @Transactional(readOnly = true)
    public Page<LossRecord> listByBranch(UUID branchId, Pageable pageable) {
        return lossRecordRepository.findByBranchId(branchId, pageable);
    }

    @Transactional
    @Audited(entityType = "LossRecord", action = AuditAction.CREATE)
    public LossRecord create(LossRecord record) {
        BranchContext.getOrganizationId().ifPresent(record::setOrganizationId);
        BranchContext.getBranchId().ifPresent(record::setBranchId);
        return lossRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public LossRecord getById(UUID id) {
        return lossRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loss record not found: " + id));
    }
}
