package com.SpringBegn.ecom_proj.repo;

import com.SpringBegn.ecom_proj.model.CartItem;
import com.SpringBegn.ecom_proj.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findBySessionId(String sessionId);

    @Modifying
    @Transactional
    void deleteBySessionId(String sessionId);

    List<CartItem> findByUser(User user);

    List<CartItem> findByProduct_Id(int productId);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    // Find specific item for user
    CartItem findByUserAndProduct_Id(User user, int productId);

    // Find specific item for session
    CartItem findBySessionIdAndProduct_Id(String sessionId, int productId);
}
