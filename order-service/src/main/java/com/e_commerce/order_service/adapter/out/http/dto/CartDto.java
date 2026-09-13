package com.e_commerce.order_service.adapter.out.http.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(
        UUID id,
        UUID userId,
        List<CartItemDto> items,
        BigDecimal total
) {}
