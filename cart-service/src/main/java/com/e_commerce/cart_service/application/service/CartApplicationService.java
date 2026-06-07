package com.e_commerce.cart_service.application.service;

import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.port.in.*;
import com.e_commerce.cart_service.domain.port.out.CartRepository;
import com.e_commerce.cart_service.domain.port.out.CatalogPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CartApplicationService implements
        AddProductCartUseCase, RemoveProductFromCartUseCase,
        UpdateCartItemQuantityUseCase, GetCartUseCase, ClearCartUseCase{

    private final CartRepository cartRepository;
    CatalogPort catalogPort;

    public CartApplicationService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart addProduct(UUID userId, UUID productId, BigDecimal price, int quantity) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.create(userId));

        price = catalogPort.getPrice(productId);
        cart.addProduct(productId, price, quantity);

        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.clear();

        cartRepository.save(cart);
    }

    @Override
    public Cart getCart(UUID userId) {

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> new Cart(userId));
    }

    @Override
    public void removeProduct(UUID userId, UUID productId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.removeProduct(productId);

        cartRepository.save(cart);

    }

    @Override
    public Cart updateQuantity(UUID userId, UUID productId, int quantity) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.updateQuantity(productId, quantity);

        cartRepository.save(cart);
        return cart;
    }
}
