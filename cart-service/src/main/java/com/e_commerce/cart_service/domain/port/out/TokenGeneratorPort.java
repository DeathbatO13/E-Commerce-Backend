package com.e_commerce.cart_service.domain.port.out;

public interface TokenGeneratorPort {
    String generateToken(String userId, String email);
}
