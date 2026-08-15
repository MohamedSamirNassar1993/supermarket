package com.supermarket.modules.platform.infrastructure.persistence;

import com.supermarket.modules.platform.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByOrganizationIdOrderByOccurredAtDesc(UUID organizationId, Pageable pageable);
}
