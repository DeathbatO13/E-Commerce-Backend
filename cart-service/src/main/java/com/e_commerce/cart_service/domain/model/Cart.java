package com.e_commerce.cart_service.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;


@Entity
@Table(name = "carts")
public class Cart{

    @Id
    private final UUID id;
    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> items;
    private final UUID userId;

    private BigDecimal totalPrice;


    public Cart(BigDecimal totalPrice, UUID id, UUID userId, List<CartItem> items) {
        this.totalPrice = totalPrice;
        this.id = id;
        this.userId = userId;
        this.items = items;
    }

    public Cart(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.items = new ArrayList<>();
        this.totalPrice = BigDecimal.ZERO;
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

    public static Cart create(UUID userId) {
        return new Cart(userId);
    }


    public void clear(){
        this.items.clear();
        this.totalPrice = BigDecimal.ZERO;
    }

}
