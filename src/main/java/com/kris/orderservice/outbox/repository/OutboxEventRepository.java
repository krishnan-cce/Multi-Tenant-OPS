package com.kris.orderservice.outbox.repository;

import com.kris.orderservice.outbox.domain.OutboxEvent;
import com.kris.orderservice.outbox.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByIdAsc(OutboxStatus status);

    List<OutboxEvent> findByStatus(OutboxStatus status);
}