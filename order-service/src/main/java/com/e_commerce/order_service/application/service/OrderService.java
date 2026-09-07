package com.e_commerce.order_service.application.service;

import com.e_commerce.order_service.adapter.in.rest.mapper.RestMapper;
import com.e_commerce.order_service.adapter.out.persistence.mapper.OrderMapper;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.out.OrderRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderService implements CreateOrderUseCase, GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderMapper mapper;

    public OrderService(OrderRepositoryPort orderRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Override
    public UUID createOrder(UUID userId, String address, List<OrderItem> items, BigDecimal total) {

        Order order = new Order(
                userId,
                address,
                items,
                total
        );

        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream().toList();
    }

    @Override
    public List<Order> getOrderByUser(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }
}
