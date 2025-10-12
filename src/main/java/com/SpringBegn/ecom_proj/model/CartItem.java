package com.SpringBegn.ecom_proj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // For logged-in users

    private String sessionId;

    public CartItem(String sessionId, Product product, int quantity) {
        this.sessionId = sessionId;
        this.product = product;
        this.quantity = quantity;
    }

    // Constructor for authenticated users
    public CartItem(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }
}
