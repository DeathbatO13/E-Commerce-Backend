package com.e_commerce.order_service.domain;

import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Invariantes y transiciones de estado de {@link Order} (CREATED → PAID / CANCELLED). */
class OrderTest {

    private final UUID userId = UUID.randomUUID();
    private final List<OrderItem> items = List.of(new OrderItem(UUID.randomUUID(), 2, BigDecimal.TEN));

    private Order newOrder() {
        return new Order(userId, "Street 123", items, BigDecimal.valueOf(20));
    }

    @Test
    void newOrder_startsAsCreated() {
        Order order = newOrder();

        assertNotNull(order.getId());
        assertNotNull(order.getCreatedAt());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void orderWithoutItems_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Order(userId, "Street 123", List.of(), BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Order(userId, "Street 123", null, BigDecimal.ONE));
    }

    @Test
    void orderWithoutRequiredData_isRejected() {
        assertThrows(NullPointerException.class, () -> new Order(null, "Street 123", items, BigDecimal.ONE));
        assertThrows(NullPointerException.class, () -> new Order(userId, null, items, BigDecimal.ONE));
        assertThrows(NullPointerException.class, () -> new Order(userId, "Street 123", items, null));
    }

    @Test
    void createdOrder_canBePaid() {
        Order order = newOrder();

        order.markAsPaid();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void paidOrder_cannotBePaidAgain() {
        Order order = newOrder();
        order.markAsPaid();

        assertThrows(IllegalStateException.class, order::markAsPaid);
    }

    @Test
    void cancelledOrder_cannotBePaid() {
        Order order = newOrder();
        order.cancel();

        assertThrows(IllegalStateException.class, order::markAsPaid);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void createdOrder_canBeCancelled() {
        Order order = newOrder();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void paidOrder_cannotBeCancelled() {
        Order order = newOrder();
        order.markAsPaid();

        assertThrows(IllegalStateException.class, order::cancel);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void items_cannotBeModifiedFromOutside() {
        Order order = newOrder();

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());
        assertEquals(1, order.getItems().size());
    }
}
