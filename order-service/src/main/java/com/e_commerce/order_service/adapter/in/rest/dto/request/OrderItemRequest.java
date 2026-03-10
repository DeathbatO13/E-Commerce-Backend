package com.e_commerce.order_service.adapter.in.rest.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(
        UUID productId,
        int quantity,
        BigDecimal price
) {
}
