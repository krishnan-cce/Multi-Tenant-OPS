package com.kris.orderservice.saga;

import com.kris.orderservice.order.domain.Order;
import com.kris.orderservice.order.domain.OrderStatus;
import com.kris.orderservice.order.repository.OrderRepository;
import com.kris.orderservice.outbox.domain.OutboxEvent;
import com.kris.orderservice.outbox.domain.OutboxStatus;
import com.kris.orderservice.outbox.repository.OutboxEventRepository;
import com.kris.orderservice.validation.TenantValidatorRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderRepository orderRepository;
    private final TenantValidatorRegistry validatorRegistry;


    @Transactional
    public boolean markInProgress(Long eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return false;
        }

        if (event.getStatus() != OutboxStatus.PENDING) {
            return false;
        }

        event.setStatus(OutboxStatus.IN_PROGRESS);
        log.info("OutboxProcessor: event {} marked IN_PROGRESS", eventId);

        return true;
    }


    @Transactional
    public void processEvent(Long eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Outbox event not found"));

        if (event.getStatus() != OutboxStatus.IN_PROGRESS) {
            log.info("OutboxProcessor: event {} is {}, skipping", eventId, event.getStatus());
            return;
        }

        // for testing IN_PROGRESS visibility
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        Order order = orderRepository.findById(event.getAggregateId())
                .orElseThrow(() -> new IllegalStateException("Order not found for event"));

        try {
            validatorRegistry.validate(order.getTenantId(), order);

            order.setStatus(OrderStatus.PROCESSED);
            event.setStatus(OutboxStatus.PROCESSED);
            event.setProcessedAt(Instant.now());
            event.setErrorMessage(null);

            log.info("OutboxProcessor: event {} PROCESSED for order {}", eventId, order.getId());

        } catch (IllegalArgumentException ex) {
            order.setStatus(OrderStatus.FAILED);
            event.setStatus(OutboxStatus.FAILED);
            event.setProcessedAt(Instant.now());
            event.setErrorMessage(ex.getMessage());

            log.warn("OutboxProcessor: event {} FAILED: {}", eventId, ex.getMessage());
        }
    }
}
