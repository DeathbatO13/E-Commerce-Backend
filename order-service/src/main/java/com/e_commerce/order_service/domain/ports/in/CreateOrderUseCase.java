package com.e_commerce.order_service.domain.ports.in;

import java.util.UUID;

public interface CreateOrderUseCase {

    UUID createOrder(UUID userId, String address, String authorizationToken);

}
