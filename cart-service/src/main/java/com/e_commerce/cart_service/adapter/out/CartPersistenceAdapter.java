package com.e_commerce.cart_service.adapter.out;

import com.e_commerce.cart_service.adapter.out.persistence.entity.CartJpaEntity;
import com.e_commerce.cart_service.adapter.out.persistence.mapper.CartMapper;
import com.e_commerce.cart_service.adapter.out.persistence.repository.CartJpaRepository;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.port.out.CartRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartPersistenceAdapter implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    public CartPersistenceAdapter(CartJpaRepository cartJpaRepository){
        this.cartJpaRepository = cartJpaRepository;
    }


    @Override
    public Cart save(Cart cart) {

        CartJpaEntity saved = cartJpaRepository.save(CartMapper.toEntity(cart));

        return CartMapper.toDomain(saved);
    }

    @Override
    public Optional<Cart> findByUserId(UUID userId) {

        return cartJpaRepository.findByUserId(userId).map(CartMapper::toDomain);
    }

    @Override
    public Optional<Cart> findById(UUID id) {

        return cartJpaRepository.findById(id).map(CartMapper::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {

        cartJpaRepository.deleteById(userId);
    }
}
