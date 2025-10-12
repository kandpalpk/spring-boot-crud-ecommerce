package com.SpringBegn.ecom_proj.controller;

import com.SpringBegn.ecom_proj.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthWebController {

    @Autowired
    private CartService cartService;

    @PostMapping("/transfer-cart")
    public ResponseEntity<?> transferCart(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String sessionId = session.getId();
            String username = auth.getName();

            // Transfer guest cart to user cart
            cartService.transferGuestCartToUser(sessionId, username);

            return ResponseEntity.ok().body("{\"message\":\"Cart transferred successfully\"}");
        }

        return ResponseEntity.badRequest().body("{\"message\":\"User not authenticated\"}");
    }
}
