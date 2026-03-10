package com.e_commerce.order_service.domain.ports.in;

import com.e_commerce.order_service.domain.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {

    UUID createOrder(
            UUID userId,
            String address,
            List<OrderItem> items,
            BigDecimal total
    );
}
