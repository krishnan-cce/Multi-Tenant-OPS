package com.kris.orderservice.tenant.repository;

import com.kris.orderservice.tenant.domain.TenantOrderConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantOrderConfigRepository extends JpaRepository<TenantOrderConfig, Long> {

    Optional<TenantOrderConfig> findByTenant_CodeIgnoreCase(String tenantCode);
}