package com.supermarket.modules.auth.application.port;

import com.supermarket.modules.auth.domain.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LoginHistoryRepositoryPort {

    LoginHistory save(LoginHistory loginHistory);

    Page<LoginHistory> findByUserId(UUID userId, Pageable pageable);
}
