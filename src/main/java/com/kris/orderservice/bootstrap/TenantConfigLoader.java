package com.kris.orderservice.bootstrap;

import com.kris.orderservice.tenant.domain.Tenant;
import com.kris.orderservice.tenant.domain.TenantOrderConfig;
import com.kris.orderservice.tenant.repository.TenantOrderConfigRepository;
import com.kris.orderservice.tenant.repository.TenantRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantConfigLoader {

    private final TenantRepository tenantRepository;
    private final TenantOrderConfigRepository configRepository;

    @PostConstruct
    public void loadTenantConfigs() {

        if (tenantRepository.count() > 0) {
            log.info("Tenants already exist, skipping bootstrap data.");
            return;
        }

        log.info("Bootstrapping TENANT_A and TENANT_B...");

        // --- TENANT A ---
        Tenant tenantA = new Tenant();
        tenantA.setCode("TENANT_A");
        tenantA.setName("Tenant A Pvt Ltd");
        tenantA.setActive(true);
        tenantA = tenantRepository.save(tenantA);

        TenantOrderConfig configA = new TenantOrderConfig();
        configA.setTenant(tenantA);
        configA.setMinAmount(BigDecimal.valueOf(100));
        configA.setMinQuantity(null);  // no quantity rule
        configRepository.save(configA);

        log.info("Inserted default config for TENANT_A");

        // --- TENANT B ---
        Tenant tenantB = new Tenant();
        tenantB.setCode("TENANT_B");
        tenantB.setName("Tenant B Pvt Ltd");
        tenantB.setActive(true);
        tenantB = tenantRepository.save(tenantB);

        TenantOrderConfig configB = new TenantOrderConfig();
        configB.setTenant(tenantB);
        configB.setMinAmount(BigDecimal.valueOf(100));
        configB.setMinQuantity(10);
        configRepository.save(configB);

        log.info("Inserted default config for TENANT_B");

        log.info("Tenant bootstrap complete.");
    }
}

