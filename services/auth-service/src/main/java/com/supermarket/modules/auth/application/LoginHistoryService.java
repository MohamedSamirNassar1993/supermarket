package com.supermarket.modules.auth.application;

import com.supermarket.modules.auth.application.port.LoginHistoryRepositoryPort;
import com.supermarket.modules.auth.domain.LoginHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepositoryPort loginHistoryRepositoryPort;

    public void recordSuccess(UUID userId, String email, String ipAddress, String userAgent) {
        LoginHistory entry = new LoginHistory();
        entry.setUserId(userId);
        entry.setEmailAttempted(email);
        entry.setSuccess(true);
        entry.setIpAddress(ipAddress);
        entry.setUserAgent(userAgent);
        entry.setOccurredAt(Instant.now());
        loginHistoryRepositoryPort.save(entry);
    }

    public void recordFailure(String email, String reason, String ipAddress, String userAgent) {
        LoginHistory entry = new LoginHistory();
        entry.setEmailAttempted(email);
        entry.setSuccess(false);
        entry.setFailureReason(reason);
        entry.setIpAddress(ipAddress);
        entry.setUserAgent(userAgent);
        entry.setOccurredAt(Instant.now());
        loginHistoryRepositoryPort.save(entry);
    }

    public Page<LoginHistory> getHistoryForUser(UUID userId, Pageable pageable) {
        return loginHistoryRepositoryPort.findByUserId(userId, pageable);
    }
}
