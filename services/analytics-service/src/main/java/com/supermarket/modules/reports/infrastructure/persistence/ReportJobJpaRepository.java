package com.supermarket.modules.reports.infrastructure.persistence;

import com.supermarket.modules.reports.domain.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportJobJpaRepository extends JpaRepository<ReportJob, UUID> {
    List<ReportJob> findByOrganizationIdAndStatusOrderByCreatedAtDesc(UUID organizationId, String status);
}
