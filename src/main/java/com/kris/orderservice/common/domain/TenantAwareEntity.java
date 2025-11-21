package com.kris.orderservice.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String tenantId;
}