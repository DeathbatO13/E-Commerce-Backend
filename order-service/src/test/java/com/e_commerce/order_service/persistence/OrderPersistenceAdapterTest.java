package com.e_commerce.order_service.persistence;

import com.e_commerce.order_service.adapter.out.persistence.OrderAdapter;
import com.e_commerce.order_service.adapter.out.persistence.repository.OrderRepository;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link OrderAdapter} contra H2: mapeo Order ↔ OrderEntity (con sus items) y actualización de estado.
 * OrderAdapter no es un @Component (lo crea BeanConfiguration), así que se construye a mano.
 */
@DataJpaTest
@ActiveProfiles("test")
class OrderPersistenceAdapterTest {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private TestEntityManager em;

    private OrderAdapter adapter;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new OrderAdapter(repository);
    }

    private Order newOrder(UUID userId) {
        return new Order(userId, "Street 123", List.of(
                new OrderItem(UUID.randomUUID(), 2, new BigDecimal("10.00")),
                new OrderItem(UUID.randomUUID(), 1, new BigDecimal("5.50"))),
                new BigDecimal("25.50"));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void savedOrder_isLoadedWithItsItems() {
        Order order = newOrder(userId);
        adapter.save(order);
        flushAndClear();

        Order loaded = adapter.findById(order.getId()).orElseThrow();

        assertEquals(userId, loaded.getUserId());
        assertEquals("Street 123", loaded.getAddress());
        assertEquals(OrderStatus.CREATED, loaded.getStatus());
        assertEquals(0, new BigDecimal("25.50").compareTo(loaded.getTotal()));
        assertEquals(order.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                loaded.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(2, loaded.getItems().size());
        assertEquals(0, new BigDecimal("25.50").compareTo(
                loaded.getItems().stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add)));
    }

    @Test
    void updatingStatus_keepsTheSameItems() {
        Order order = newOrder(userId);
        adapter.save(order);
        flushAndClear();

        // Mismo flujo que OrderService.payOrder: leer, cambiar estado y guardar
        Order loaded = adapter.findById(order.getId()).orElseThrow();
        loaded.markAsPaid();
        adapter.save(loaded);
        flushAndClear();

        Order reloaded = adapter.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, reloaded.getStatus());
        // OrderMapper.toEntity rearma los items sin id: no deben duplicarse al guardar de nuevo
        assertEquals(2, reloaded.getItems().size());
        assertEquals(2, em.getEntityManager()
                .createQuery("select count(i) from OrderItemEntity i", Long.class)
                .getSingleResult());
    }

    @Test
    void findByUserId_returnsOnlyThatUsersOrders() {
        adapter.save(newOrder(userId));
        adapter.save(newOrder(userId));
        adapter.save(newOrder(UUID.randomUUID()));
        flushAndClear();

        assertEquals(2, adapter.findByUserId(userId).size());
        assertEquals(3, adapter.findAll().size());
    }

    @Test
    void unknownOrder_isEmpty() {
        assertTrue(adapter.findById(UUID.randomUUID()).isEmpty());
    }
}
