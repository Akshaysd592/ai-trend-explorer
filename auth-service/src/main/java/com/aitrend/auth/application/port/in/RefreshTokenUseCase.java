package com.aitrend.auth.application.port.in;

import com.aitrend.auth.domain.model.AuthToken;

public interface RefreshTokenUseCase {
    AuthToken refreshToken(String refreshToken);
}
