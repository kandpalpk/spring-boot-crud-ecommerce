package com.SpringBegn.ecom_proj.controller;

import com.SpringBegn.ecom_proj.model.Product;
import com.SpringBegn.ecom_proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    private ProductService service;

    //Home Page
    @GetMapping("/")
    public String home(Model model){

        List<Product> products = service.getAllProducts();
        model.addAttribute("products",products);
        model.addAttribute("newProduct", new Product());
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model){
       List<Product> products = service.searchProducts(keyword);
       model.addAttribute("products",products);
       model.addAttribute("newProduct",new Product());
       model.addAttribute("searchKeyword", keyword);

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
}
