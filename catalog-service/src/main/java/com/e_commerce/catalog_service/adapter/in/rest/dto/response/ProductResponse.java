package com.e_commerce.catalog_service.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        UUID categoryId,
        boolean active
) {
}
