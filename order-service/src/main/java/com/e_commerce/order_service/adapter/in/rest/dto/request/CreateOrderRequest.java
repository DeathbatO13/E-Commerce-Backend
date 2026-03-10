package com.e_commerce.order_service.adapter.in.rest.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
    String address,
    BigDecimal total,
    List<OrderItemRequest> items
){
}
