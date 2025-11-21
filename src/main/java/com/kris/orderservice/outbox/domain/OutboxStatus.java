package com.kris.orderservice.outbox.domain;


public enum OutboxStatus {
    PENDING,
    IN_PROGRESS,
    PROCESSED,
    FAILED
}