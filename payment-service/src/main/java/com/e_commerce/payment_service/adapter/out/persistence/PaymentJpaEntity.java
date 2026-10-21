package com.e_commerce.payment_service.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentJpaEntity {

    @Id
    private UUID id;

    private UUID orderId;

    private BigDecimal amount;

    private String status;

    private String transactionId;

    private LocalDateTime createdAt;
}
