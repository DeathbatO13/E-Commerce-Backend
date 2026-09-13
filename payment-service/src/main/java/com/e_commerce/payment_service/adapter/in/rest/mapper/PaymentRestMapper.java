package com.e_commerce.payment_service.adapter.in.rest.mapper;

import com.e_commerce.payment_service.adapter.in.rest.dto.PaymentResponse;
import com.e_commerce.payment_service.domain.model.Payment;

public class PaymentRestMapper {

    public static PaymentResponse toResponse(Payment domain) {
        if (domain == null) return null;
        return new PaymentResponse(
                domain.getId(),
                domain.getOrderId(),
                domain.getAmount(),
                domain.getStatus().name(),
                domain.getTransactionId(),
                domain.getCreatedAt()
        );
    }
}
