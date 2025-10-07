package com.SpringBegn.ecom_proj.controller;

import com.SpringBegn.ecom_proj.model.Product;
import com.SpringBegn.ecom_proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
}
