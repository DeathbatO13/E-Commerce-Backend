package com.e_commerce.order_service.domain.ports.out;

import com.e_commerce.order_service.adapter.out.http.dto.CartDto;

import java.util.UUID;

public interface CartPort {

    CartDto getActiveCart(UUID userId, String token);

    void clearCart(UUID userId, String token);

}
