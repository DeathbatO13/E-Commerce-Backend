package com.e_commerce.order_service.service;

import com.e_commerce.order_service.application.service.OrderService;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepositoryPort repository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void createOrderSuccessfully(){

        UUID userId = UUID.randomUUID();

        List<OrderItem> items = List.of(
                new OrderItem(
                        UUID.randomUUID(),
                        2,
                        BigDecimal.valueOf(10)
                )
        );

        BigDecimal total = BigDecimal.valueOf(20);

        Order savedOrder = new Order(
                userId,
                "Street 123",
                items,
                total
        );

        when(repository.save(any(Order.class))).thenReturn(savedOrder);

        UUID orderId = orderService.createOrder(
                userId,
                "Street 123",
                items,
                total
        );

        assertNotNull(orderId);
    }

    @Test
    void createOrderError(){

        UUID userId = UUID.randomUUID();

        List<OrderItem> items = List.of(
                new OrderItem(
                        UUID.randomUUID(),
                        1,
                        BigDecimal.valueOf(15)
                )
        );

        BigDecimal total = BigDecimal.valueOf(15);

        when(repository.save(any()))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(
                        userId,
                        "Address",
                        items,
                        total
                )
        );
    }


    @Test
    void createOrderWithoutItemShouldFail(){


        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(
                        userId,
                        "Address",
                        List.of(),
                        BigDecimal.TEN
                )
        );

    }


    @Test
    void getAllOrdersSuccessfully(){

        List<Order> orders = List.of(
                new Order(
                        UUID.randomUUID(),
                        "Address",
                        List.of(
                                new OrderItem(
                                        UUID.randomUUID(),
                                        1,
                                        BigDecimal.TEN
                                )
                        ),
                        BigDecimal.TEN
                )
        );

        when(repository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());

        verify(repository).findAll();
    }


}
