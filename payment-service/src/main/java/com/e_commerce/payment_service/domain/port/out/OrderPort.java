package com.e_commerce.payment_service.domain.port.out;

import com.e_commerce.payment_service.domain.model.OrderSummary;

import java.util.UUID;

public interface OrderPort {

    /**
     * Obtiene el pedido con el token del usuario, así order-service aplica su regla de ownership
     * (un CLIENT solo puede ver, y por lo tanto pagar, sus propios pedidos).
     *
     * @param authorizationToken header Authorization del usuario ("Bearer ...")
     */
    OrderSummary getOrder(UUID orderId, String authorizationToken);

    void markOrderAsPaid(UUID orderId);
}
