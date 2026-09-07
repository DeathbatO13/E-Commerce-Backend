package com.e_commerce.order_service.adapter.in.rest.mapper;

import com.e_commerce.order_service.adapter.in.rest.dto.request.OrderItemRequest;
import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderItemResponse;
import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;

import java.util.List;

public class RestMapper {

    public static List<OrderItem> toDomainItems(List<OrderItemRequest> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(i -> new OrderItem(
                        i.productId(),
                        i.quantity(),
                        i.price()
                )).toList();
    }

    public static OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream()
                    .map(item -> new OrderItemResponse(
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice()
                    ))
                    .toList()
                : List.of();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus() != null ? order.getStatus().name() : null,
                itemResponses,
                order.getTotal(),
                order.getCreatedAt()
        );
    }
}
