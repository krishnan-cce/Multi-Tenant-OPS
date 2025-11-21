package com.kris.orderservice.tenant.repository;

import com.kris.orderservice.tenant.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByCodeIgnoreCase(String code);
}
