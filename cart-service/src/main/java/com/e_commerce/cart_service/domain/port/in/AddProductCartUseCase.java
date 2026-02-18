package com.e_commerce.cart_service.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface AddProductCartUseCase{

    void addProduct(UUID userId, UUID productId, BigDecimal price, int quantity);
}
