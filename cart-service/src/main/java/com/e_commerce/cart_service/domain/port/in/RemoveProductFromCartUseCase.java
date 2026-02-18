package com.e_commerce.cart_service.domain.port.in;

import java.util.UUID;

public interface RemoveProductFromCartUseCase {

    void removeProduct(UUID userId, UUID productId);
}
