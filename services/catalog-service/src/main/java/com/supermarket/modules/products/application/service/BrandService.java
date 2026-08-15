package com.supermarket.modules.products.application.service;

import com.supermarket.modules.platform.application.OrganizationGuard;
import com.supermarket.modules.products.application.dto.BrandDtos;
import com.supermarket.modules.products.application.mapper.ProductMapper;
import com.supermarket.modules.products.infrastructure.persistence.BrandEntity;
import com.supermarket.modules.products.infrastructure.persistence.BrandRepository;
import com.supermarket.shared.exception.BusinessRuleException;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final OrganizationGuard organizationGuard;

    @Transactional(readOnly = true)
    public Page<BrandDtos.Response> list(UUID organizationId, Boolean active, Pageable pageable) {
        organizationGuard.requireOrganization(organizationId);
        Page<BrandEntity> page = active == null
                ? brandRepository.findByOrganizationId(organizationId, pageable)
                : brandRepository.findByOrganizationIdAndActive(organizationId, active, pageable);
        return page.map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BrandDtos.Response get(UUID organizationId, UUID id) {
        return productMapper.toResponse(findEntity(organizationId, id));
    }

    @Transactional
    public BrandDtos.Response create(BrandDtos.CreateRequest request) {
        organizationGuard.requireOrganization(request.getOrganizationId());
        if (brandRepository.existsByOrganizationIdAndCode(request.getOrganizationId(), request.getCode())) {
            throw new BusinessRuleException("Brand code already exists: " + request.getCode());
        }
        BrandEntity entity = productMapper.toEntity(request);
        return productMapper.toResponse(brandRepository.save(entity));
    }

    @Transactional
    public BrandDtos.Response update(UUID organizationId, UUID id, BrandDtos.UpdateRequest request) {
        BrandEntity entity = findEntity(organizationId, id);
        productMapper.updateBrand(request, entity);
        return productMapper.toResponse(brandRepository.save(entity));
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        BrandEntity entity = findEntity(organizationId, id);
        entity.setActive(false);
        brandRepository.save(entity);
    }

    BrandEntity findEntity(UUID organizationId, UUID id) {
        organizationGuard.requireOrganization(organizationId);
        return brandRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
    }
}
