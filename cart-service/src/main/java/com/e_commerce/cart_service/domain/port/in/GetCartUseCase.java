package com.e_commerce.cart_service.domain.port.in;

import com.e_commerce.cart_service.domain.model.Cart;

import java.util.UUID;

public interface GetCartUseCase {

    Cart getCart(UUID userId);
}
