package com.supermarket.modules.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettingsEntity, UUID> {

    Optional<OrganizationSettingsEntity> findByOrganizationId(UUID organizationId);
}
