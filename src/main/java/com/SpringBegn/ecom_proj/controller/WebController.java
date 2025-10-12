package com.SpringBegn.ecom_proj.controller;

import com.SpringBegn.ecom_proj.model.*;
import com.SpringBegn.ecom_proj.service.CartService;
import com.SpringBegn.ecom_proj.service.OrderService;
import com.SpringBegn.ecom_proj.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.SpringBegn.ecom_proj.model.Order;
import com.SpringBegn.ecom_proj.repo.OrderRepository;
import com.SpringBegn.ecom_proj.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    private ProductService service;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    //Home Page
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Product> productPage = service.getAllProducts(pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("newProduct", new Product());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "index";
    }

    private User getCurrentUser(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return userRepository.findByUsername(auth.getName()).orElse(null);
        }
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail != null) {
            return userRepository.findByEmail(userEmail).orElse(null);
        }

        return null;
    }


    @GetMapping("/search")
    public String search(@RequestParam String keyword,
                         Model model,
                         @RequestParam(defaultValue="0") int page,
                         @RequestParam(defaultValue="6") int size,
                         @RequestParam(defaultValue = "id") String sortBy,
                         @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = service.searchProducts(keyword, pageable);
        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("newProduct", new Product());
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "index";

    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute Product product, @RequestParam("image")MultipartFile imageFile){

        try{
            service.addProduct(product, imageFile);
        } catch (IOException e){
            System.out.println("Error in adding product: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model){
        Product product = service.getProductById(id);
        model.addAttribute("product",product);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable int id, @ModelAttribute Product product, @RequestParam("image") MultipartFile imageFile){
        try{
            service.updateProduct(id, product, imageFile);
        } catch (IOException e) {
            System.out.println("Error updating product: "+e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id){
        service.deleteProduct(id);
        return "redirect:/";
    }

    //Add to Cart
    @PostMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable int id, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {

        Product product = service.getProductById(id);
        String sessionId = session.getId();

        cartService.addToCart(sessionId, product, quantity);

        return "redirect:/?added=true";
    }

    //View Cart
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session){
        String sessionId = session.getId();
        List<CartItem> cartItems = cartService.getCartItems(sessionId);

        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems",cartItems);
        model.addAttribute("total",total);

        return "cart";
    }

    //Remove From Cart
    @GetMapping("/remove-from-cart/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session){
        String sessionId = session.getId();
        cartService.removeFromCart(sessionId, productId);
        return "redirect:/cart";
    }

    //Checkout Page
    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session){
        String sessionId = session.getId();
        List<CartItem> cartItems = cartService.getCartItems(sessionId);

        if(cartItems.isEmpty()){
            return "redirect:/cart";
        }

        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems",cartItems);
        model.addAttribute("total",total);
        model.addAttribute("order",new Order());

        return "checkout";
    }

    //Process Order

    @PostMapping("/place-order")
    public String placeOrder(@ModelAttribute Order order, HttpSession session) {
        try {
            String sessionId = session.getId();
            List<CartItem> cartItems = cartService.getCartItems(sessionId);

            if (cartItems.isEmpty()) {
                return "redirect:/cart?error=empty";
            }

            BigDecimal total = cartItems.stream()
                    .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalAmount(total);

            // Get current user and set their email
            User currentUser = getCurrentUser(session);
            if (currentUser != null) {
                // Override with user's actual email if they're logged in
                order.setCustomerEmail(currentUser.getEmail());

                // If they didn't fill name, use their profile name
                if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
                    String fullName = (currentUser.getFirstName() != null ? currentUser.getFirstName() + " " : "") +
                            (currentUser.getLastName() != null ? currentUser.getLastName() : "");
                    if (!fullName.trim().isEmpty()) {
                        order.setCustomerName(fullName.trim());
                    }
                }
            }

            Order savedOrder = orderService.createOrder(order, sessionId);

            return "redirect:/order-success/" + savedOrder.getOrderNumber();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/checkout?error=true";
        }
    }


    @GetMapping("/orders")
    public String viewOrders(Model model, HttpSession session) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login?redirect=/orders";
        }

        // Try multiple ways to find orders
        List<Order> orders = new ArrayList<>();

        // Method 1: By email
        orders.addAll(orderRepository.findByCustomerEmailOrderByOrderDateDesc(currentUser.getEmail()));

        // Method 2: If no orders found, try by name (if user filled checkout form)
        if (orders.isEmpty() && currentUser.getFirstName() != null) {
            String fullName = currentUser.getFirstName() + " " +
                    (currentUser.getLastName() != null ? currentUser.getLastName() : "");
            orders.addAll(orderRepository.findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc(fullName));
        }

        // Method 3: For debugging - show recent orders if still empty (remove this later)
        if (orders.isEmpty()) {
            System.out.println("No orders found for user: " + currentUser.getEmail());
            System.out.println("Checking all recent orders...");
            List<Order> allOrders = orderRepository.findAll();
            System.out.println("Total orders in system: " + allOrders.size());
            allOrders.forEach(o -> System.out.println("Order: " + o.getOrderNumber() + ", Email: " + o.getCustomerEmail()));
        }

        model.addAttribute("orders", orders);
        model.addAttribute("user", currentUser);

        return "orders";
    }

    @GetMapping("/debug-orders")
    public String debugOrders(Model model, HttpSession session) {
        User currentUser = getCurrentUser(session);
        List<Order> allOrders = orderRepository.findAll();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allOrders", allOrders);
        model.addAttribute("userEmail", currentUser != null ? currentUser.getEmail() : "No user");

        return "debug-orders"; // Create this template or just return JSON
    }




    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login?redirect=/profile";
        }

        model.addAttribute("user", currentUser);
        return "profile";
    }



    //Order Success
    @GetMapping("/order-success/{orderNumber}")
    public String orderSuccess(@PathVariable String orderNumber, Model model){
        Order order = orderService.getOrderByNumber(orderNumber);
        model.addAttribute("order",order);
        return "order-success";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Clear session
        session.invalidate();

        // Clear Spring Security context
        SecurityContextHolder.clearContext();

        return "redirect:/?logout=true";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }


}
