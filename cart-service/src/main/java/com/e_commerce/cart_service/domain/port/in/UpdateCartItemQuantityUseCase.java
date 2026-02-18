package com.e_commerce.cart_service.domain.port.in;

import java.util.UUID;

public interface UpdateCartItemQuantityUseCase {

    void updateQuantity(UUID userId, UUID productId, int quantity);
}
