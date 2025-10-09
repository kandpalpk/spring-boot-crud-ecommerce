package com.SpringBegn.ecom_proj.repo;

import com.SpringBegn.ecom_proj.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Order findByOrderNumber(String orderNumber);
}
