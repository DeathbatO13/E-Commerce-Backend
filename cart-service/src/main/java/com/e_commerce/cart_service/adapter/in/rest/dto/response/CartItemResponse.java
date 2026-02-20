package com.e_commerce.cart_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        int quantity,
        BigDecimal price
) {
}
