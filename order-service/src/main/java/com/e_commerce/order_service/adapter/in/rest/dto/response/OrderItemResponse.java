package com.e_commerce.order_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productId,
        int quantity,
        BigDecimal price
) {
}
