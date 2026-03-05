package com.e_commerce.order_service.domain.ports.in;

import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderUseCase {

    Order createOrder(UUID userId, String address, List<OrderItem> items);

    List<Order> getAllOrders();

    Optional<Order> getOrderById(UUID id);
}
