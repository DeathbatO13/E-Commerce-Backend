package com.e_commerce.payment_service.config;

import com.e_commerce.payment_service.application.service.PaymentApplicationService;
import com.e_commerce.payment_service.domain.port.in.ProcessPaymentUseCase;
import com.e_commerce.payment_service.domain.port.out.OrderPort;
import com.e_commerce.payment_service.domain.port.out.PaymentRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(
            PaymentRepositoryPort paymentRepositoryPort,
            OrderPort orderPort) {
        return new PaymentApplicationService(paymentRepositoryPort, orderPort);
    }
}
