package com.aitrend.auth.application.port.out;

import com.aitrend.auth.domain.model.AuthToken;
import com.aitrend.auth.domain.model.User;

public interface TokenProviderPort {
    AuthToken generateToken(User user);
    String extractEmail(String token);
    boolean validateToken(String token);
}
