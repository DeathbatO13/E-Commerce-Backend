package com.e_commerce.auth_service.domain.port.in;

import com.e_commerce.auth_service.domain.model.User;

public interface RegisterUserUseCase {

    User register(String email, String rawPassword);
}
