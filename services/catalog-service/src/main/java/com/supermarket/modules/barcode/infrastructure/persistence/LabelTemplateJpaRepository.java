package com.supermarket.modules.barcode.infrastructure.persistence;

import com.supermarket.modules.barcode.domain.LabelTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabelTemplateJpaRepository extends JpaRepository<LabelTemplate, UUID> {
    List<LabelTemplate> findByOrganizationIdAndActiveTrue(UUID organizationId);

    Optional<LabelTemplate> findByOrganizationIdAndCode(UUID organizationId, String code);
}
