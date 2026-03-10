package com.e_commerce.order_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        String status,
        List<OrderItemResponse> items,
        BigDecimal total,
        LocalDateTime createdAt
) {
}
