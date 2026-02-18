package com.e_commerce.cart_service.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class Cart{

    private final UUID id;
    private final UUID userId;
    private final List<CartItem> items;
    private BigDecimal totalPrice;


    public Cart(BigDecimal totalPrice, UUID id, UUID userId, List<CartItem> items) {
        this.totalPrice = totalPrice;
        this.id = id;
        this.userId = userId;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    private Optional<CartItem> findItem(UUID productId){
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    private void recalculateTotal(){

        totalPrice = items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void addProduct(UUID productId, BigDecimal price, int quantity){

        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        Optional<CartItem> existingItem = findItem(productId);

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            items.add(new CartItem(UUID.randomUUID(), productId, price, quantity));
        }

        recalculateTotal();
    }

    public void updateQuantity(UUID productId, int quantity){

        CartItem item = findItem(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        if (quantity <= 0) {
            items.remove(item);
        } else {
            item.updateQuantity(quantity);
        }

        recalculateTotal();
    }

    public void removeProduct(UUID productId){

        items.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal();
    }

}
