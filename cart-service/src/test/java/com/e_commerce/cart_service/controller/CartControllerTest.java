package com.e_commerce.cart_service.controller;

import com.e_commerce.cart_service.adapter.in.rest.CartController;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.port.in.*;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCartUseCase getCartUseCase;

    @MockitoBean
    private AddProductCartUseCase addProductCartUseCase;

    @MockitoBean
    private UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;

    @MockitoBean
    private ClearCartUseCase clearCartUseCase;

    @MockitoBean
    private RemoveProductFromCartUseCase removeProductFromCartUseCase;


    @Test
    void shouldReturnCart() throws Exception{

        UUID userId = UUID.randomUUID();

        Cart cart = new Cart(userId);
        cart.addProduct(UUID.randomUUID(), BigDecimal.valueOf(100), 2);

        when(getCartUseCase.getCart(userId)).thenReturn(cart);

        mockMvc.perform(get("/cart")
                .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").value(200));
    }


    /**
     * @throws Exception 
     */
    @Test
    void shouldAddProductToCart() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String body = """
        {
          "productId": "%s",
          "quantity": 2
        }
        """.formatted(productId);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .requestAttr("userId", userId))
                .andExpect(status().isOk());

        verify(addProductCartUseCase).addProduct(
                eq(userId),
                eq(productId),
                eq(2)
        );
    }


    @Test
    void shouldAddProductToCart() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String body = """
        {
          "productId": "%s",
          "quantity": 2
        }
        """.formatted(productId);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .requestAttr("userId", userId))
                .andExpect(status().isOk());

        verify(addProductCartUseCase).addProduct(
                eq(userId),
                eq(productId),
                eq(2)
        );
    }

}
