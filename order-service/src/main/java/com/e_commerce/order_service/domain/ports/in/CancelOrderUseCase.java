package com.e_commerce.order_service.domain.ports.in;

import java.util.UUID;

public interface CancelOrderUseCase {

    void cancelOrder(UUID orderId);

}
