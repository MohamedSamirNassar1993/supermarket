package com.supermarket.modules.inventory.application.service;

import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.application.mapper.InventoryMapper;
import com.supermarket.modules.inventory.domain.ValuationMethod;
import com.supermarket.modules.inventory.infrastructure.persistence.OrganizationSettingsEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.OrganizationSettingsRepository;
import com.supermarket.modules.platform.application.OrganizationGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationSettingsService {

    private final OrganizationSettingsRepository settingsRepository;
    private final OrganizationGuard organizationGuard;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public InventoryDtos.SettingsResponse get(UUID organizationId) {
        return inventoryMapper.toResponse(getOrCreate(organizationId));
    }

    @Transactional
    public InventoryDtos.SettingsResponse update(UUID organizationId, InventoryDtos.SettingsUpdateRequest request) {
        OrganizationSettingsEntity settings = getOrCreate(organizationId);
        if (request.getValuationMethod() != null) {
            settings.setValuationMethod(request.getValuationMethod());
        }
        if (request.getAllowNegativeStock() != null) {
            settings.setAllowNegativeStock(request.getAllowNegativeStock());
        }
        if (request.getLowStockAlertEnabled() != null) {
            settings.setLowStockAlertEnabled(request.getLowStockAlertEnabled());
        }
        if (request.getExpiryAlertDays() != null) {
            settings.setExpiryAlertDays(request.getExpiryAlertDays());
        }
        return inventoryMapper.toResponse(settingsRepository.save(settings));
    }

    @Transactional(readOnly = true)
    public ValuationMethod getValuationMethod(UUID organizationId) {
        return getOrCreate(organizationId).getValuationMethod();
    }

    @Transactional(readOnly = true)
    public boolean isNegativeStockAllowed(UUID organizationId) {
        return getOrCreate(organizationId).isAllowNegativeStock();
    }

    OrganizationSettingsEntity getOrCreate(UUID organizationId) {
        organizationGuard.requireOrganization(organizationId);
        return settingsRepository.findByOrganizationId(organizationId)
                .orElseGet(() -> {
                    OrganizationSettingsEntity settings = new OrganizationSettingsEntity();
                    settings.setOrganizationId(organizationId);
                    settings.setValuationMethod(ValuationMethod.FIFO);
                    return settingsRepository.save(settings);
                });
    }
}
