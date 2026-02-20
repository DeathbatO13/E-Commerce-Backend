package com.e_commerce.cart_service.adapter.in.rest.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record AddItemRequest(
        UUID productId,
        BigDecimal price,
        int quantity
) {
}
