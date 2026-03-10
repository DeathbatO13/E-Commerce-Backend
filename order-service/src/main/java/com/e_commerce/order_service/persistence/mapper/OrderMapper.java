package com.e_commerce.order_service.persistence.mapper;

import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;
import com.e_commerce.order_service.domain.model.OrderStatus;
import com.e_commerce.order_service.persistence.entity.OrderEntity;
import com.e_commerce.order_service.persistence.entity.OrderItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static Order toDomain(OrderEntity entity) {

        List<OrderItem> items = entity.getItems()
                .stream()
                .map(i -> new OrderItem(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getPrice()
                ))
                .collect(Collectors.toList());

        return new Order(
                entity.getId(),
                entity.getUserId(),
                entity.getAddress(),
                items,
                entity.getTotal(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }

    public static OrderEntity toEntity(Order order) {

        OrderEntity entity = new OrderEntity();

        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setAddress(order.getAddress());
        entity.setTotal(order.getTotal());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt());

        List<OrderItemEntity> items = order.getItems()
                .stream()
                .map(item -> {
                    OrderItemEntity e = new OrderItemEntity();
                    e.setProductId(item.getProductId());
                    e.setQuantity(item.getQuantity());
                    e.setPrice(item.getPrice());
                    e.setOrder(entity);
                    return e;
                })
                .collect(Collectors.toList());

        entity.setItems(items);

        return entity;
    }
}
