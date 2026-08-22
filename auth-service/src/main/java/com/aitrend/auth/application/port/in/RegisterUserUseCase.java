package com.aitrend.auth.application.port.in;

import com.aitrend.auth.domain.model.User;

public interface RegisterUserUseCase {
    User registerUser(RegisterUserCommand command);
}
