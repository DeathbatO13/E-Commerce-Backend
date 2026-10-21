package com.e_commerce.payment_service.adapter.out.persistence;

import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.model.PaymentStatus;

public class PaymentMapper {

    public static Payment toDomain(PaymentJpaEntity entity) {
        if (entity == null) return null;
        return new Payment(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getTransactionId(),
                entity.getCreatedAt()
        );
    }

    public static PaymentJpaEntity toEntity(Payment domain) {
        if (domain == null) return null;
        return new PaymentJpaEntity(
                domain.getId(),
                domain.getOrderId(),
                domain.getAmount(),
                domain.getStatus().name(),
                domain.getTransactionId(),
                domain.getCreatedAt()
        );
    }
}
