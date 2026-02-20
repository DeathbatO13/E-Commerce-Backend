package com.e_commerce.cart_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID userId,
        List<CartItemResponse> item,
        BigDecimal total
) {
}
