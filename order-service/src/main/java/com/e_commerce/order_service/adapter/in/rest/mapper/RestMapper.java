package com.e_commerce.order_service.adapter.in.rest.mapper;

import com.e_commerce.order_service.adapter.in.rest.dto.request.OrderItemRequest;
import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;


import java.util.List;

public class RestMapper{

    public static List<OrderItem> toDomainItems(List<OrderItemRequest> items){
        return items.stream()
                .map( i -> new OrderItem(
                        i.productId(),
                        i.quantity(),
                        i.price()
                )).toList();
    }

    public OrderResponse toResponse(Order order){

<<<<<<< HEAD
        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getItems().stream().map().toList(),
                order.getTotal(),
                order.getCreatedAt()
        );

        return response;
=======
        OrderResponse response = new OrderResponse();
>>>>>>> c0d2c85 (Comenzando a corregir)
    }
}
