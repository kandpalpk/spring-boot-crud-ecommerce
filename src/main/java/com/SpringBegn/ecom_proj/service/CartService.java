package com.SpringBegn.ecom_proj.service;

import com.SpringBegn.ecom_proj.model.CartItem;
import com.SpringBegn.ecom_proj.model.Product;
import com.SpringBegn.ecom_proj.model.User;
import com.SpringBegn.ecom_proj.repo.CartItemRepository;
import com.SpringBegn.ecom_proj.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    public void addToCart(String sessionId, Product product, int quantity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User is logged in
            addToUserCart(auth.getName(), product, quantity);
        } else {
            // Guest user
            addToGuestCart(sessionId, product, quantity);
        }
    }

    private void addToUserCart(String username, Product product, int quantity) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return;

        CartItem existingItem = cartItemRepository.findByUserAndProduct_Id(user, product.getId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            cartItemRepository.save(newItem);
        }
    }

    private void addToGuestCart(String sessionId, Product product, int quantity) {
        CartItem existingItem = cartItemRepository.findBySessionIdAndProduct_Id(sessionId, product.getId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(sessionId, product, quantity);
            cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getCartItems(String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User is logged in
            User user = userRepository.findByUsername(auth.getName()).orElse(null);
            if (user != null) {
                return cartItemRepository.findByUser(user);
            }
        }

        // Guest user
        return cartItemRepository.findBySessionId(sessionId);
    }

    public void removeFromCart(String sessionId, Long productId) {
        List<CartItem> cartItems = getCartItems(sessionId);
        cartItems.stream()
                .filter(item -> item.getProduct().getId() == productId.intValue())
                .findFirst()
                .ifPresent(cartItemRepository::delete);
    }

    public void clearCart(String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User is logged in
            User user = userRepository.findByUsername(auth.getName()).orElse(null);
            if (user != null) {
                cartItemRepository.deleteByUser(user);
            }
        } else {
            // Guest user
            cartItemRepository.deleteBySessionId(sessionId);
        }
    }

    // Transfer guest cart to user cart when they log in
    public void transferGuestCartToUser(String sessionId, String username) {
        List<CartItem> guestItems = cartItemRepository.findBySessionId(sessionId);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && !guestItems.isEmpty()) {
            for (CartItem guestItem : guestItems) {
                CartItem existingUserItem = cartItemRepository.findByUserAndProduct_Id(user, guestItem.getProduct().getId());

                if (existingUserItem != null) {
                    // Merge quantities
                    existingUserItem.setQuantity(existingUserItem.getQuantity() + guestItem.getQuantity());
                    cartItemRepository.save(existingUserItem);
                } else {
                    // Create new user cart item
                    CartItem newUserItem = new CartItem(user, guestItem.getProduct(), guestItem.getQuantity());
                    cartItemRepository.save(newUserItem);
                }
            }

            // Clear guest cart
            cartItemRepository.deleteBySessionId(sessionId);
        }
    }
}
