package com.e_commerce.cart_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        boolean active
) {}
