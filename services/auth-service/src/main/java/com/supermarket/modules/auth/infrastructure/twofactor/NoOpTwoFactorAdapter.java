package com.supermarket.modules.auth.infrastructure.twofactor;

import com.supermarket.modules.auth.application.port.TwoFactorPort;
import com.supermarket.modules.auth.domain.User;
import org.springframework.stereotype.Component;

@Component
public class NoOpTwoFactorAdapter implements TwoFactorPort {

    @Override
    public boolean isEnabled(User user) {
        return user.isTwoFactorEnabled();
    }

    @Override
    public boolean verify(User user, String code) {
        return !user.isTwoFactorEnabled() || (code != null && !code.isBlank());
    }

    @Override
    public void sendChallenge(User user) {
        // No-op stub for future SMS/TOTP integration
    }
}
