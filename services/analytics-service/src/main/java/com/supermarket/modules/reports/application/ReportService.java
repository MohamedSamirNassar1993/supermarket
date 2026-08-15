package com.supermarket.modules.reports.application;

import com.supermarket.modules.reports.domain.ReportJob;
import com.supermarket.modules.reports.infrastructure.messaging.ReportJobPublisher;
import com.supermarket.modules.reports.infrastructure.persistence.ReportJobJpaRepository;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportJobJpaRepository reportJobRepository;
    private final ReportJobPublisher reportJobPublisher;

    @Transactional
    public ReportJob submitJob(ReportJob job) {
        BranchContext.getOrganizationId().ifPresent(job::setOrganizationId);
        BranchContext.getBranchId().ifPresent(job::setBranchId);
        if (job.getParameters() == null) {
            job.setParameters(new HashMap<>());
        }
        job.setStatus("QUEUED");
        ReportJob saved = reportJobRepository.save(job);
        reportJobPublisher.publish(saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public ReportJob getJob(UUID id) {
        return reportJobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report job not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportJob> listQueued(UUID organizationId) {
        return reportJobRepository.findByOrganizationIdAndStatusOrderByCreatedAtDesc(organizationId, "QUEUED");
    }
}
