package com.e_commerce.cart_service.adapter.in.rest.mapper;

import com.e_commerce.cart_service.adapter.in.rest.dto.response.CartItemResponse;
import com.e_commerce.cart_service.adapter.in.rest.dto.response.CartResponse;
import com.e_commerce.cart_service.domain.model.Cart;

import java.util.List;

public class CartRestMapper{

    public static CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                items,
                cart.getTotalPrice()
        );
    }
}
