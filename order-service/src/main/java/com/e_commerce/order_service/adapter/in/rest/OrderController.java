package com.e_commerce.order_service.adapter.in.rest;

import com.e_commerce.order_service.adapter.in.rest.dto.request.CreateOrderRequest;
import com.e_commerce.order_service.adapter.in.rest.dto.response.OrderResponse;
import com.e_commerce.order_service.adapter.in.rest.mapper.RestMapper;
import com.e_commerce.order_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.PayOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final PayOrderUseCase payOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            PayOrderUseCase payOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @RequestHeader("Authorization") String token,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        UUID orderId = createOrderUseCase.createOrder(
                userId,
                request.address(),
                token
        );

        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable UUID id) {
        payOrderUseCase.payOrder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            Authentication authentication) {

        findOwnedOrder(id, authentication);

        cancelOrderUseCase.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        List<Order> orders;
        if (isAdmin(authentication)) {
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
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID id,
            Authentication authentication) {

        return ResponseEntity.ok(RestMapper.toResponse(findOwnedOrder(id, authentication)));
    }

    /**
     * Busca el pedido y verifica que pertenezca al usuario autenticado (ADMIN y SUPER_ADMIN ven todos).
     * GlobalExceptionHandler traduce las excepciones a 404 y 403.
     */
    private Order findOwnedOrder(UUID id, Authentication authentication) {
        Order order = getOrderUseCase.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        UUID currentUserId = UUID.fromString(authentication.getName());
        if (!isAdmin(authentication) && !order.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Order " + id + " belongs to another user");
        }
        return order;
    }

    private static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }
}
