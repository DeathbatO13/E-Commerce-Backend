package com.e_commerce.payment_service.domain.port.out;

import java.util.UUID;

public interface OrderPort {

    void markOrderAsPaid(UUID orderId);

}
