package com.e_commerce.order_service.service;

import java.util.Optional;
import org.mockito.ArgumentCaptor;
import com.e_commerce.order_service.domain.model.OrderStatus;
import com.e_commerce.order_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.order_service.domain.model.CartSnapshot;
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

        CartSnapshot cart = new CartSnapshot(userId, List.of(new CartSnapshot.Item(productId, 2)));

        when(cartPort.getActiveCart(userId, token)).thenReturn(cart);
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

        CartSnapshot emptyCart = new CartSnapshot(userId, List.of());
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

    @Test
    void createOrder_usesCatalogPricesForTotal() {
        UUID userId = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        String token = "Bearer token123";
        when(cartPort.getActiveCart(userId, token)).thenReturn(new CartSnapshot(userId,
                List.of(new CartSnapshot.Item(p1, 2), new CartSnapshot.Item(p2, 1))));
        when(catalogPort.getProductPrice(p1)).thenReturn(new BigDecimal("15.00"));
        when(catalogPort.getProductPrice(p2)).thenReturn(new BigDecimal("4.50"));
        when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.createOrder(userId, "Street 123", token);

        ArgumentCaptor<Order> saved = ArgumentCaptor.forClass(Order.class);
        verify(repository).save(saved.capture());
        assertEquals(0, new BigDecimal("34.50").compareTo(saved.getValue().getTotal()));
        assertEquals(OrderStatus.CREATED, saved.getValue().getStatus());
        assertEquals(2, saved.getValue().getItems().size());
    }

    @Test
    void createOrder_whenCatalogFails_doesNotSaveNorClearCart() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String token = "Bearer token123";
        when(cartPort.getActiveCart(userId, token))
                .thenReturn(new CartSnapshot(userId, List.of(new CartSnapshot.Item(productId, 1))));
        when(catalogPort.getProductPrice(productId))
                .thenThrow(new ResourceNotFoundException("Product not found: " + productId));

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(userId, "Street 123", token));

        verify(repository, never()).save(any());
        verify(cartPort, never()).clearCart(any(), any());
    }

    @Test
    void payOrder_marksCreatedOrderAsPaid() {
        Order order = newOrder();
        when(repository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.payOrder(order.getId());

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(repository).save(order);
    }

    @Test
    void payOrder_ofCancelledOrder_failsWithoutSaving() {
        Order order = newOrder();
        order.cancel();
        when(repository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.payOrder(order.getId()));
        verify(repository, never()).save(any());
    }

    @Test
    void payOrCancelMissingOrder_fails() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.payOrder(id));
        assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(id));
        verify(repository, never()).save(any());
    }

    @Test
    void cancelOrder_cancelsCreatedOrder() {
        Order order = newOrder();
        when(repository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.cancelOrder(order.getId());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(repository).save(order);
    }

    @Test
    void cancelOrder_ofPaidOrder_failsWithoutSaving() {
        Order order = newOrder();
        order.markAsPaid();
        when(repository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(order.getId()));
        verify(repository, never()).save(any());
    }

    private Order newOrder() {
        return new Order(UUID.randomUUID(), "Street 123",
                List.of(new OrderItem(UUID.randomUUID(), 1, BigDecimal.TEN)), BigDecimal.TEN);
    }
}
