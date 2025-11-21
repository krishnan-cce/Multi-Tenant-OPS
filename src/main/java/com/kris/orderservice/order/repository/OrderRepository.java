package com.kris.orderservice.order.repository;

import com.kris.orderservice.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}