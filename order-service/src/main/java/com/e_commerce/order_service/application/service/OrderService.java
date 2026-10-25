package com.e_commerce.order_service.application.service;

import com.e_commerce.order_service.adapter.out.http.dto.CartDto;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.PayOrderUseCase;
import com.e_commerce.order_service.domain.ports.out.CartPort;
import com.e_commerce.order_service.domain.ports.out.CatalogPort;
import com.e_commerce.order_service.domain.ports.out.OrderRepositoryPort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderService implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase, CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final CartPort cartPort;
    private final CatalogPort catalogPort;

    public OrderService(OrderRepositoryPort orderRepository, CartPort cartPort, CatalogPort catalogPort) {
        this.orderRepository = orderRepository;
        this.cartPort = cartPort;
        this.catalogPort = catalogPort;
    }

    @Override
    public UUID createOrder(UUID userId, String address, String authorizationToken) {
        CartDto cart = cartPort.getActiveCart(userId, authorizationToken);

        if (cart == null || cart.items() == null || cart.items().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found for user: " + userId);
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (var itemDto : cart.items()) {
            BigDecimal verifiedPrice = catalogPort.getProductPrice(itemDto.productId());
            OrderItem item = new OrderItem(itemDto.productId(), itemDto.quantity(), verifiedPrice);
            items.add(item);
            calculatedTotal = calculatedTotal.add(item.getSubtotal());
        }

        Order order = new Order(
                userId,
                address,
                items,
                calculatedTotal
        );

        Order savedOrder = orderRepository.save(order);

        cartPort.clearCart(userId, authorizationToken);

        return savedOrder.getId();
    }

    @Override
    public void payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.markAsPaid();
        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.cancel();
        orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
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
