package com.kris.orderservice.tenant.domain;

import com.kris.orderservice.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_code", columnNames = "code")
})
@Getter
@Setter
@NoArgsConstructor
public class Tenant extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String code;   // "TENANT_A", "TENANT_B"

    @Column(nullable = false, length = 100)
    private String name;   // "Tenant A Pvt Ltd"

    @Column(nullable = false)
    private boolean active = true;
}
