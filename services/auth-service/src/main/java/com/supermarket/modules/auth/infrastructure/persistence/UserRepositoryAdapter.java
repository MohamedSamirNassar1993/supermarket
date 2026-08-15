package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.application.port.UserRepositoryPort;
import com.supermarket.modules.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findWithRolesById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmailOrUsername(String identifier) {
        return userJpaRepository.findByEmailOrUsername(identifier);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userJpaRepository.findAllWithRoles(pageable);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return userJpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, UUID id) {
        return userJpaRepository.existsByUsernameAndIdNot(username, id);
    }
}
