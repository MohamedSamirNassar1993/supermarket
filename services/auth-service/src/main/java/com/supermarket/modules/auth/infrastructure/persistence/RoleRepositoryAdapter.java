package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.application.port.RoleRepositoryPort;
import com.supermarket.modules.auth.domain.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<Role> findById(UUID id) {
        return roleJpaRepository.findWithPermissionsById(id);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return roleJpaRepository.findByCode(code);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Role> findAllById(Set<UUID> ids) {
        return roleJpaRepository.findAllById(ids);
    }

    @Override
    public Role save(Role role) {
        return roleJpaRepository.save(role);
    }

    @Override
    public void delete(Role role) {
        roleJpaRepository.delete(role);
    }

    @Override
    public boolean existsByCode(String code) {
        return roleJpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, UUID id) {
        return roleJpaRepository.existsByCodeAndIdNot(code, id);
    }

    public Role getById(UUID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
    }
}
