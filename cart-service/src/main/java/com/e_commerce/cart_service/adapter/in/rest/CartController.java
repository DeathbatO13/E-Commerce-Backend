package com.e_commerce.cart_service.adapter.in.rest;


import com.e_commerce.cart_service.adapter.in.rest.dto.request.AddItemRequest;
import com.e_commerce.cart_service.adapter.in.rest.dto.request.UpdateItemRequest;
import com.e_commerce.cart_service.adapter.in.rest.dto.response.CartResponse;
import com.e_commerce.cart_service.adapter.in.rest.mapper.CartRestMapper;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.port.in.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final GetCartUseCase getCartUseCase;
    private final AddProductCartUseCase addProductUseCase;
    private final UpdateCartItemQuantityUseCase updateQuantityUseCase;
    private final RemoveProductFromCartUseCase removeProductUseCase;
    private final ClearCartUseCase clearCartUseCase;

    public CartController(
            GetCartUseCase getCartUseCase,
            AddProductCartUseCase addProductUseCase,
            UpdateCartItemQuantityUseCase updateQuantityUseCase,
            RemoveProductFromCartUseCase removeProductUseCase,
            ClearCartUseCase clearCartUseCase
    ) {
        this.getCartUseCase = getCartUseCase;
        this.addProductUseCase = addProductUseCase;
        this.updateQuantityUseCase = updateQuantityUseCase;
        this.removeProductUseCase = removeProductUseCase;
        this.clearCartUseCase = clearCartUseCase;
    }

    private UUID extractUserId(Authentication authentication) {

        return UUID.fromString(authentication.getName());
    }


    @GetMapping
    public CartResponse getCart(Authentication authentication) {

        UUID userId = extractUserId(authentication);

        Cart cart = getCartUseCase.getCart(userId);

        return CartRestMapper.toResponse(cart);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Authentication authentication) {

        UUID userId = extractUserId(authentication);

        clearCartUseCase.clearCart(userId);

    }

    @PostMapping("/items")
    public CartResponse addItem(
            @RequestBody AddItemRequest request,
            Authentication authentication
    ) {

        UUID userId = extractUserId(authentication);

        Cart cart = addProductUseCase.addProduct(
                userId,
                request.productId(),
                request.price(),
                request.quantity()
        );

        return CartRestMapper.toResponse(cart);
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateItem(
            @PathVariable UUID productId,
            @RequestBody UpdateItemRequest request,
            Authentication authentication
    ) {

        UUID userId = extractUserId(authentication);

        Cart cart = updateQuantityUseCase.updateQuantity(
                userId,
                productId,
                request.quantity()
        );

        return CartRestMapper.toResponse(cart);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @PathVariable UUID productId,
            Authentication authentication
    ) {

        UUID userId = extractUserId(authentication);

        removeProductUseCase.removeProduct(userId, productId);
    }
}
