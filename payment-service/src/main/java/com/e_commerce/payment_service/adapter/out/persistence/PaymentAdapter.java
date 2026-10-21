package com.e_commerce.payment_service.adapter.out.persistence;

import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.port.out.PaymentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository repository;

    public PaymentAdapter(PaymentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = PaymentMapper.toEntity(payment);
        PaymentJpaEntity saved = repository.save(entity);
        return PaymentMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return repository.findById(id).map(PaymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).map(PaymentMapper::toDomain);
    }
}
