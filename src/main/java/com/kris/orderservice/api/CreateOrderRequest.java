package com.kris.orderservice.api;

import java.math.BigDecimal;

public record CreateOrderRequest(
        String tenantId,
        BigDecimal amount,
        Integer quantity
) { }