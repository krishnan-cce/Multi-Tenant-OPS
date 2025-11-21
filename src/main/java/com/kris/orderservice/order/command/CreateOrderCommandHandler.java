package com.kris.orderservice.order.command;

import com.kris.orderservice.order.domain.Order;
import com.kris.orderservice.order.domain.OrderStatus;
import com.kris.orderservice.order.repository.OrderRepository;
import com.kris.orderservice.outbox.domain.OutboxEvent;
import com.kris.orderservice.outbox.domain.OutboxEventType;
import com.kris.orderservice.outbox.domain.OutboxStatus;
import com.kris.orderservice.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Long handle(CreateOrderCommand command) {
        // 1. Save Order in PENDING
        Order order = new Order();
        order.setTenantId(command.tenantId());
        order.setAmount(command.amount());
        order.setQuantity(command.quantity());
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);

        // 2. Save Outbox event in same transaction
        OutboxEvent event = new OutboxEvent();
        event.setTenantId(command.tenantId());
        event.setAggregateType("ORDER");
        event.setAggregateId(saved.getId());
        event.setEventType(OutboxEventType.ORDER_CREATED);
        event.setStatus(OutboxStatus.PENDING);

        Map<String, Object> payload = Map.of(
                "orderId", saved.getId(),
                "tenantId", saved.getTenantId()
        );
        try {
            event.setPayload(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }

        outboxEventRepository.save(event);

        return saved.getId();
    }
}

