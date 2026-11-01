package com.e_commerce.order_service.domain.ports.out;

import com.e_commerce.order_service.domain.model.CartSnapshot;

import java.util.UUID;

public interface CartPort {

    CartSnapshot getActiveCart(UUID userId, String token);

    void clearCart(UUID userId, String token);

}
