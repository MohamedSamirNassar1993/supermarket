package com.supermarket.modules.products.application.service;

import com.supermarket.modules.platform.application.OrganizationGuard;
import com.supermarket.modules.products.application.dto.CategoryDtos;
import com.supermarket.modules.products.application.mapper.ProductMapper;
import com.supermarket.modules.products.infrastructure.persistence.CategoryEntity;
import com.supermarket.modules.products.infrastructure.persistence.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final OrganizationGuard organizationGuard;

    @Transactional(readOnly = true)
    public Page<CategoryDtos.Response> list(UUID organizationId, Boolean active, Pageable pageable) {
        organizationGuard.requireOrganization(organizationId);
        Page<CategoryEntity> page = active == null
                ? categoryRepository.findByOrganizationId(organizationId, pageable)
                : categoryRepository.findByOrganizationIdAndActive(organizationId, active, pageable);
        return page.map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoryDtos.Response get(UUID organizationId, UUID id) {
        return productMapper.toResponse(findEntity(organizationId, id));
    }

    @Transactional
    public CategoryDtos.Response create(CategoryDtos.CreateRequest request) {
        organizationGuard.requireOrganization(request.getOrganizationId());
        if (categoryRepository.existsByOrganizationIdAndCode(request.getOrganizationId(), request.getCode())) {
            throw new BusinessRuleException("Category code already exists: " + request.getCode());
        }
        if (request.getParentId() != null) {
            findEntity(request.getOrganizationId(), request.getParentId());
        }
        CategoryEntity entity = productMapper.toEntity(request);
        return productMapper.toResponse(categoryRepository.save(entity));
    }

    @Transactional
    public CategoryDtos.Response update(UUID organizationId, UUID id, CategoryDtos.UpdateRequest request) {
        CategoryEntity entity = findEntity(organizationId, id);
        if (request.getParentId() != null && !request.getParentId().equals(entity.getParentId())) {
            findEntity(organizationId, request.getParentId());
        }
        productMapper.updateCategory(request, entity);
        return productMapper.toResponse(categoryRepository.save(entity));
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        CategoryEntity entity = findEntity(organizationId, id);
        entity.setActive(false);
        categoryRepository.save(entity);
    }

    CategoryEntity findEntity(UUID organizationId, UUID id) {
        organizationGuard.requireOrganization(organizationId);
        return categoryRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
