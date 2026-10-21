package com.e_commerce.cart_service.adapter.out.persistence.mapper;

import com.e_commerce.cart_service.adapter.out.persistence.entity.CartItemJpaEntity;
import com.e_commerce.cart_service.adapter.out.persistence.entity.CartJpaEntity;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartMapper {

    public static CartJpaEntity toEntity(Cart cart) {

        CartJpaEntity cartEntity = new CartJpaEntity(
                cart.getId(),
                cart.getUserId(),
                cart.getTotalPrice(),
                new java.util.ArrayList<>()
        );

        if (cart.getItems() != null) {
            List<CartItemJpaEntity> items = cart.getItems().stream()
                    .map(item -> new CartItemJpaEntity(
                            item.getId(),
                            item.getProductId(),
                            item.getPrice(),
                            item.getQuantity(),
                            cartEntity
                    )).toList();

            cartEntity.getItems().addAll(items);
        }

        return cartEntity;
    }


    public static Cart toDomain(CartJpaEntity entity){

        List<CartItem> items = entity.getItems().stream()
                .map(item -> new CartItem(
                        item.getId(),
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity()
                )).toList();

        return new Cart(
                entity.getTotalPrice(),
                entity.getId(),
                entity.getUserId(),
                items
        );
    }
}
