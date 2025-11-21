package com.kris.orderservice.outbox.domain;


import com.kris.orderservice.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_status", columnList = "status"),
                @Index(name = "idx_outbox_type", columnList = "eventType")
        })
@Getter
@Setter
@NoArgsConstructor
public class OutboxEvent extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String tenantId;

    @Column(nullable = false, length = 50)
    private String aggregateType;  // e.g. "ORDER"

    @Column(nullable = false)
    private Long aggregateId;      // orderId

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OutboxEventType eventType;

    @Lob
    @Column(nullable = false)
    private String payload;        // JSON string

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    private Instant processedAt;

    @Column(length = 1000)
    private String errorMessage;
}
