package com.kris.orderservice.validation;


import com.kris.orderservice.order.domain.Order;

public interface TenantOrderValidator {

    /**
     * Whether this validator supports the given tenantId.
     */
    boolean supports(String tenantId);

    /**
     * Validates the order. Throws IllegalArgumentException for validation errors.
     */
    void validate(Order order);
}