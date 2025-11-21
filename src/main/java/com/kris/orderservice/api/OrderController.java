package com.kris.orderservice.api;

import com.kris.orderservice.order.command.CreateOrderCommand;
import com.kris.orderservice.order.command.CreateOrderCommandHandler;
import com.kris.orderservice.order.query.GetOrderQuery;
import com.kris.orderservice.order.query.GetOrderQueryHandler;
import com.kris.orderservice.order.query.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderCommandHandler createHandler;
    private final GetOrderQueryHandler getHandler;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@RequestBody CreateOrderRequest request) {
        Long id = createHandler.handle(
                new CreateOrderCommand(
                        request.tenantId(),
                        request.amount(),
                        request.quantity()
                )
        );
        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable Long id) {
        OrderResponseDto dto = getHandler.handle(new GetOrderQuery(id));
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}

