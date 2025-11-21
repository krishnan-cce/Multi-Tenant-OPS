package com.kris.orderservice.order.query;

import com.kris.orderservice.order.domain.Order;
import com.kris.orderservice.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class GetOrderQueryHandler {

    private final OrderRepository orderRepository;

    public GetOrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponseDto handle(GetOrderQuery query) {
        Order order = orderRepository.findById(query.id())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return new OrderResponseDto(
                order.getId(),
                order.getTenantId(),
                order.getAmount(),
                order.getQuantity(),
                order.getStatus()
        );
    }

}

