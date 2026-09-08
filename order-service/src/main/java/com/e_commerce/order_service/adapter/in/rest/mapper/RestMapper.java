package com.e_commerce.order_service.adapter.in.rest.mapper;

import com.e_commerce.order_service.adapter.in.rest.dto.request.OrderItemRequest;
import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;
import com.e_commerce.order_service.adapter.out.persistence.mapper.OrderMapper;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.model.OrderItem;


import java.util.List;

public class RestMapper{

    private final OrderMapper mapper;

    public RestMapper(OrderMapper mapper){
       this.mapper = mapper;
    }

    public static List<OrderItem> toDomainItems(List<OrderItemRequest> items){
        return items.stream()
                .map( i -> new OrderItem(
                        i.productId(),
                        i.quantity(),
                        i.price()
                )).toList();
    }

    /*public OrderResponse toResponse(Order order){

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getItems().stream().map().toList(),
                order.getTotal(),
                order.getCreatedAt()
        );

        return response;

    }*/
}
