package com.e_commerce.order_service.adapter.in.rest;


import com.e_commerce.order_service.adapter.in.rest.dto.request.CreateOrderRequest;
import com.e_commerce.order_service.adapter.in.rest.mapper.RestMapper;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController{

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase){
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @RequestHeader("X-User-Id")UUID userId,
            @RequestBody CreateOrderRequest request
            ){

        var items = RestMapper.toDomainItems(request.items());

        UUID orderId = createOrderUseCase.createOrder(
                userId,
                request.address(),
                items,
                request.total()
        );

        return ResponseEntity.created(null).build();

    }

    @GetMapping
    public ResponseEntity<?> getOrders(){
        return ResponseEntity.ok(getOrderUseCase.getAllOrders());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID id){

        return getOrderUseCase.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
