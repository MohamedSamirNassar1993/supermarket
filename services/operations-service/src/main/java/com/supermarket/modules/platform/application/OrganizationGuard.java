package com.supermarket.modules.platform.application;

import com.supermarket.modules.platform.infrastructure.persistence.OrganizationJpaRepository;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationGuard {

    private final OrganizationJpaRepository organizationRepository;

    public void requireOrganization(UUID organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization", organizationId);
        }
    }
}
