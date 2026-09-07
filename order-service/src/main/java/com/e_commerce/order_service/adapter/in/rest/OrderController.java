package com.e_commerce.order_service.adapter.in.rest;

import com.e_commerce.order_service.adapter.in.rest.dto.request.CreateOrderRequest;
import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;
import com.e_commerce.order_service.adapter.in.rest.mapper.RestMapper;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            Authentication authentication) {

        var items = RestMapper.toDomainItems(request.items());

        UUID userId = UUID.fromString(authentication.getName());

        UUID orderId = createOrderUseCase.createOrder(
                userId,
                request.address(),
                items,
                request.total()
        );

        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();

    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_SUPER_ADMIN"));

        List<Order> orders;
        if (isAdmin) {
            orders = getOrderUseCase.getAllOrders();
        } else {
            orders = getOrderUseCase.getOrderByUser(userId);
        }

        List<OrderResponse> responseList = orders.stream()
                .map(RestMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {

        return getOrderUseCase.getOrderById(id)
                .map(RestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
