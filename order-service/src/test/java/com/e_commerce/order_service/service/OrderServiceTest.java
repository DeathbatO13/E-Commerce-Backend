package com.e_commerce.order_service.service;

import com.e_commerce.order_service.adapter.out.http.dto.CartDto;
import com.e_commerce.order_service.adapter.out.http.dto.CartItemDto;
import com.e_commerce.order_service.application.service.OrderService;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.ports.out.CartPort;
import com.e_commerce.order_service.domain.ports.out.CatalogPort;
import com.e_commerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepositoryPort repository;

    @Mock
    private CartPort cartPort;

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String token = "Bearer token123";

        CartItemDto cartItemDto = new CartItemDto(productId, 2, BigDecimal.valueOf(10));
        CartDto cartDto = new CartDto(UUID.randomUUID(), userId, List.of(cartItemDto), BigDecimal.valueOf(20));

        when(cartPort.getActiveCart(userId, token)).thenReturn(cartDto);
        when(catalogPort.getProductPrice(productId)).thenReturn(BigDecimal.valueOf(10));

        List<OrderItem> items = List.of(new OrderItem(productId, 2, BigDecimal.valueOf(10)));
        Order savedOrder = new Order(userId, "Street 123", items, BigDecimal.valueOf(20));
        when(repository.save(any(Order.class))).thenReturn(savedOrder);

        UUID orderId = orderService.createOrder(userId, "Street 123", token);

        assertNotNull(orderId);
        verify(cartPort).clearCart(userId, token);
    }

    @Test
    void createOrderWithoutCartItemsShouldFail() {
        UUID userId = UUID.randomUUID();
        String token = "Bearer token123";

        CartDto emptyCart = new CartDto(UUID.randomUUID(), userId, List.of(), BigDecimal.ZERO);
        when(cartPort.getActiveCart(userId, token)).thenReturn(emptyCart);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(userId, "Address", token)
        );
    }

    @Test
    void getAllOrdersSuccessfully() {
        List<Order> orders = List.of(
                new Order(
                        UUID.randomUUID(),
                        "Address",
                        List.of(new OrderItem(UUID.randomUUID(), 1, BigDecimal.TEN)),
                        BigDecimal.TEN
                )
        );

        when(repository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }
}
