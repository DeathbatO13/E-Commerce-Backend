package com.e_commerce.order_service.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
    @NotBlank(message = "Address is required")
    String address
) {}
