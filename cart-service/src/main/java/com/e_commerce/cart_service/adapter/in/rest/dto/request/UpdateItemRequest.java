package com.e_commerce.cart_service.adapter.in.rest.dto.request;

import jakarta.validation.constraints.Positive;

/**
 * Según cart.yaml la cantidad mínima es 1; para quitar un producto se usa DELETE /cart/items/{productId}.
 */
public record UpdateItemRequest(
        @Positive int quantity
) {
}
