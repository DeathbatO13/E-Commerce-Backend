package com.e_commerce.cart_service.domain.port.in;

import com.e_commerce.cart_service.domain.model.Cart;

import java.util.UUID;

public interface AddProductCartUseCase{

    Cart addProduct(UUID userId, UUID productId, int quantity);
}
