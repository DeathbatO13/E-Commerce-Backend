package com.e_commerce.order_service.domain.ports.out;

import com.e_commerce.order_service.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findAll();

    List<Order> findByUserId(UUID userId);

}
