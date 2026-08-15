package com.supermarket.modules.platform.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<BranchEntity, UUID> {

    Optional<BranchEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    List<BranchEntity> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
