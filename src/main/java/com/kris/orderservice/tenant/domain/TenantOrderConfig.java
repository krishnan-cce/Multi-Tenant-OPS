package com.kris.orderservice.tenant.domain;

import com.kris.orderservice.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tenant_order_configs")
@Getter
@Setter
@NoArgsConstructor
public class TenantOrderConfig extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal minAmount;

    @Column
    private Integer minQuantity;   // nullable: if null, no quantity condition

}

