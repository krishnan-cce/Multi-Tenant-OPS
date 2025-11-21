package com.kris.orderservice.order.query;

import com.kris.orderservice.order.domain.OrderStatus;

import java.math.BigDecimal;

public record OrderResponseDto(
        Long id,
        String tenantId,
        BigDecimal amount,
        Integer quantity,
        OrderStatus status
) { }
