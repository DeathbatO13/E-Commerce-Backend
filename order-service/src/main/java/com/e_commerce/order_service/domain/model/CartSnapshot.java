package com.e_commerce.order_service.domain.model;

import java.util.List;
import java.util.UUID;

/**
 * Lo que order-service necesita saber del carrito activo para crear un pedido.
 * <p>
 * No incluye precios a propósito: OrderService los vuelve a consultar en catalog-service.
 * El adaptador HTTP traduce la respuesta de cart-service a este modelo, así la capa
 * de aplicación no depende de los DTOs del adaptador.
 * </p>
 */
public record CartSnapshot(UUID userId, List<Item> items) {

    public CartSnapshot {
        items = items != null ? List.copyOf(items) : List.of();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public record Item(UUID productId, int quantity) {
    }
}
