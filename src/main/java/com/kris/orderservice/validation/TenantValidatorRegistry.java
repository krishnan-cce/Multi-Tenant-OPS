package com.kris.orderservice.validation;

import com.kris.orderservice.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantValidatorRegistry {

    private final List<TenantOrderValidator> validators;

    public void validate(String tenantId, Order order) {

        TenantOrderValidator validator = validators.stream()
                .filter(v -> v.supports(tenantId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No validator configured"));

        validator.validate(order);
    }
}