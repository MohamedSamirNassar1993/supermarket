package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.application.port.UserBranchRepositoryPort;
import com.supermarket.modules.auth.domain.UserBranch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserBranchRepositoryAdapter implements UserBranchRepositoryPort {

    private final UserBranchJpaRepository userBranchJpaRepository;

    @Override
    public List<UserBranch> findByUserId(UUID userId) {
        return userBranchJpaRepository.findByIdUserId(userId);
    }

    @Override
    public void saveAll(List<UserBranch> branches) {
        userBranchJpaRepository.saveAll(branches);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        userBranchJpaRepository.deleteByUserId(userId);
    }
}
