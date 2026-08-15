package com.supermarket.modules.auth.application.port;

import com.supermarket.modules.auth.domain.User;

public interface TwoFactorPort {

    boolean isEnabled(User user);

    boolean verify(User user, String code);

    void sendChallenge(User user);
}
