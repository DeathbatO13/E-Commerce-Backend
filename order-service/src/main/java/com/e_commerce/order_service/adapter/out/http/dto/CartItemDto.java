package com.e_commerce.order_service.adapter.out.http.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID productId,
        int quantity,
        BigDecimal price
) {}
