package com.SpringBegn.ecom_proj.repo;

import com.SpringBegn.ecom_proj.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
