package com.e_commerce.catalog_service.adapter.in.rest.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(String name,
                                   String description,
                                   BigDecimal price,
                                   int stock,
                                   UUID categoryId) {
}
