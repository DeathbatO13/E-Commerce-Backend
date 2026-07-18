package com.e_commerce.order_service.domain.ports.in;

import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase{

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrderByUser(UUID userId);

    Optional<OrderResponse> getOrderById(UUID id);

}
