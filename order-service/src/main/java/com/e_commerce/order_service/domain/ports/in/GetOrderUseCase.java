package com.e_commerce.order_service.domain.ports.in;

import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;
import com.e_commerce.order_service.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase{

    List<Order> getAllOrders();

    List<Order> getOrderByUser(UUID userId);

    Optional<Order> getOrderById(UUID id);

}
