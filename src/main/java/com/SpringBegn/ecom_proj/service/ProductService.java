package com.SpringBegn.ecom_proj.service;

import com.SpringBegn.ecom_proj.model.CartItem;
import com.SpringBegn.ecom_proj.model.Product;
import com.SpringBegn.ecom_proj.repo.CartItemRepository;
import com.SpringBegn.ecom_proj.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Page<Product> getAllProducts(Pageable page) {
        return repo.findAll(page);
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        product.setImageName(imageFile.getName());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());
        return repo.save(product);
    }

    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {
        Product existingProduct = repo.findById(id).orElse(null);
        System.out.println(existingProduct.toString());
        if (existingProduct == null) return null;

        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }

        if (product.getDesc() != null) {
            existingProduct.setDesc(product.getDesc());
        }

        if (product.getBrand() != null) {
            existingProduct.setBrand(product.getBrand());
        }

        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }

        if (product.getCategory() != null) {
            existingProduct.setCategory(product.getCategory());
        }

        if (product.getQuantity() > 0) { // or != 0 depending on your business logic
            existingProduct.setQuantity(product.getQuantity());
        }

        if (product.getReleaseDate() != null) {
            existingProduct.setReleaseDate(product.getReleaseDate());
        }

        existingProduct.setProductAvailable(product.isProductAvailable());

        existingProduct.setImageName(imageFile.getName());
        existingProduct.setImageType(imageFile.getContentType());
        existingProduct.setImageData(imageFile.getBytes());

        return repo.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(int id) {
        // Remove all cart items containing this product first
        List<CartItem> cartItems = cartItemRepository.findByProduct_Id(id);
        cartItemRepository.deleteAll(cartItems);
        repo.deleteById(id);
    }


    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable){
        return repo.searchProducts(keyword,pageable);
    }

}
