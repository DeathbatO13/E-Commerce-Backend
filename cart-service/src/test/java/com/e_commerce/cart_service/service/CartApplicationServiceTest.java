package com.e_commerce.cart_service.service;

import com.e_commerce.cart_service.application.service.CartApplicationService;
import com.e_commerce.cart_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.port.out.CartRepository;
import com.e_commerce.cart_service.domain.port.out.CatalogPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartApplicationServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CatalogPort catalogPort;

    private CartApplicationService service;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new CartApplicationService(cartRepository, catalogPort);
        lenient().when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void addProduct_usesPriceFromCatalog() {
        when(catalogPort.getPrice(productId)).thenReturn(BigDecimal.valueOf(25));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Cart cart = service.addProduct(userId, productId, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(0, BigDecimal.valueOf(25).compareTo(cart.getItems().get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(50).compareTo(cart.getTotalPrice()));
    }

    @Test
    void addProduct_toExistingCart_addsItem() {
        UUID otherProduct = UUID.randomUUID();
        Cart existing = new Cart(userId);
        existing.addProduct(otherProduct, BigDecimal.TEN, 1);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(catalogPort.getPrice(productId)).thenReturn(BigDecimal.valueOf(5));

        Cart cart = service.addProduct(userId, productId, 3);

        assertEquals(2, cart.getItems().size());
        assertEquals(0, BigDecimal.valueOf(25).compareTo(cart.getTotalPrice()));
    }

    @Test
    void addProduct_withUnknownProduct_doesNotSaveCart() {
        when(catalogPort.getPrice(productId))
                .thenThrow(new ResourceNotFoundException("Product not found or inactive: " + productId));

        assertThrows(ResourceNotFoundException.class, () -> service.addProduct(userId, productId, 1));

        verify(cartRepository, never()).save(any());
    }

    @Test
    void updateQuantity_returnsSavedCart() {
        Cart existing = new Cart(userId);
        existing.addProduct(productId, BigDecimal.TEN, 1);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        Cart cart = service.updateQuantity(userId, productId, 4);

        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(0, BigDecimal.valueOf(40).compareTo(cart.getTotalPrice()));
        verify(cartRepository).save(existing);
    }
}
