package com.e_commerce.payment_service.adapter.in.rest;

import com.e_commerce.payment_service.adapter.in.rest.dto.PaymentResponse;
import com.e_commerce.payment_service.adapter.in.rest.dto.ProcessPaymentRequest;
import com.e_commerce.payment_service.adapter.in.rest.mapper.PaymentRestMapper;
import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.port.in.ProcessPaymentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentController(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody @Valid ProcessPaymentRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Payment payment = processPaymentUseCase.processPayment(
                request.orderId(),
                request.amount(),
                token
        );

        return ResponseEntity.ok(PaymentRestMapper.toResponse(payment));
    }
}
