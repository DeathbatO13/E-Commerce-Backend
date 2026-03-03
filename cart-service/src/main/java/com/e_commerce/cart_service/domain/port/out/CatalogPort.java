package com.e_commerce.cart_service.domain.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface CatalogPort {

        BigDecimal getPrice(UUID productId);

}
