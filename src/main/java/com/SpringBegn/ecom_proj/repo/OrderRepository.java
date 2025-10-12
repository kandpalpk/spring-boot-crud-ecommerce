package com.SpringBegn.ecom_proj.repo;

import com.SpringBegn.ecom_proj.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String email);
    Order findByOrderNumber(String orderNumber);
    List<Order> findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc(String name);

}
