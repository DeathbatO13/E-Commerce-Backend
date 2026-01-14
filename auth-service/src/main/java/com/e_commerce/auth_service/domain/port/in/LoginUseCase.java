package com.e_commerce.auth_service.domain.port.in;

public interface LoginUseCase {

    String login(String email, String rawPassword);
}
