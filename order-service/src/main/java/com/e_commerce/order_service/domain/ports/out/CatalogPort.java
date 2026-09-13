package com.e_commerce.order_service.domain.ports.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface CatalogPort {

    BigDecimal getProductPrice(UUID productId);

}
