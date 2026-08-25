package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.OrderList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderList, Integer> {
}
