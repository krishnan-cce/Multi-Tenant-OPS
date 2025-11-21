package com.kris.orderservice.validation;

import com.kris.orderservice.order.domain.Order;
import com.kris.orderservice.tenant.domain.TenantOrderConfig;
import com.kris.orderservice.tenant.repository.TenantOrderConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseTenantOrderValidator implements TenantOrderValidator {

    private final TenantOrderConfigRepository configRepository;

    @Override
    public boolean supports(String tenantId) {
        return configRepository.findByTenant_CodeIgnoreCase(tenantId).isPresent();
    }

    @Override
    public void validate(Order order) {
        TenantOrderConfig config = configRepository
                .findByTenant_CodeIgnoreCase(order.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("No config found for tenant"));

        if (order.getAmount() == null ||
                order.getAmount().compareTo(config.getMinAmount()) <= 0) {
            throw new IllegalArgumentException("Amount must be > " + config.getMinAmount());
        }

        if (config.getMinQuantity() != null) {
            if (order.getQuantity() == null ||
                    order.getQuantity() <= config.getMinQuantity()) {
                throw new IllegalArgumentException("Quantity must be > " + config.getMinQuantity());
            }
        }
    }
}
