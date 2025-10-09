package com.SpringBegn.ecom_proj.service;

import com.SpringBegn.ecom_proj.model.CartItem;
import com.SpringBegn.ecom_proj.model.Product;
import com.SpringBegn.ecom_proj.repo.CartItemRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class CartService {

    private CartItemRepository cartItemRepository;

    public void addToCart(String sessionId, Product product, int quantity){
        List<CartItem> cartItems = cartItemRepository.findBySessionId(sessionId);

        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item-> item.getProduct().getId()==product.getId())
                .findFirst();

        if(existingItem.isPresent()){
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity()+quantity);
            cartItemRepository.save(item);
        }
        else{
            CartItem newItem = new CartItem();
            newItem.setSessionId(sessionId);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getCartItems(String sessionId){
        return cartItemRepository.findBySessionId(sessionId);
    }

    public void removeFromCart(String sessionId, Long productId){
        List<CartItem> cartItems = cartItemRepository.findBySessionId(sessionId);
        cartItems.stream()
                .filter(item->item.getProduct().getId()==productId.intValue())
                .findFirst()
                .ifPresent(cartItemRepository::delete);
    }

    public void clearCart(String sessionId) {
        cartItemRepository.deleteBySessionId(sessionId);
    }
}
