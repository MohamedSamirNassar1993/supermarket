package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.application.port.LoginHistoryRepositoryPort;
import com.supermarket.modules.auth.domain.LoginHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoginHistoryRepositoryAdapter implements LoginHistoryRepositoryPort {

    private final LoginHistoryJpaRepository loginHistoryJpaRepository;

    @Override
    public LoginHistory save(LoginHistory loginHistory) {
        return loginHistoryJpaRepository.save(loginHistory);
    }

    @Override
    public Page<LoginHistory> findByUserId(UUID userId, Pageable pageable) {
        return loginHistoryJpaRepository.findByUserIdOrderByOccurredAtDesc(userId, pageable);
    }
}
