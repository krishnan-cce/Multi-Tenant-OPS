package com.kris.orderservice.order.command;


import java.math.BigDecimal;

public record CreateOrderCommand(
        String tenantId,
        BigDecimal amount,
        Integer quantity
) { }