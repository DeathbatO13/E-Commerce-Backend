package com.e_commerce.cart_service.domain.port.in;

import java.util.UUID;

public interface ClearCartUseCase{

    void clearCart(UUID userId);
}
