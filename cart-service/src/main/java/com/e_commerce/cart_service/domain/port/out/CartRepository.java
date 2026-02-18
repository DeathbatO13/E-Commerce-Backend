package com.e_commerce.cart_service.domain.port.out;

import com.e_commerce.cart_service.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
