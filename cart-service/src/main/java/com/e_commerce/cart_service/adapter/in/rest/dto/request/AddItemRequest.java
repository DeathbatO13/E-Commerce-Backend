package com.e_commerce.cart_service.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

/**
 * El precio no se recibe del cliente: CartApplicationService lo consulta a catalog-service.
 */
public record AddItemRequest(
        @NotNull UUID productId,
        @Positive int quantity
) {
}
