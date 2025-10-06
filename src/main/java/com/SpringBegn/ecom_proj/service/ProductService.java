package com.SpringBegn.ecom_proj.service;

import com.SpringBegn.ecom_proj.model.Product;
import com.SpringBegn.ecom_proj.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;
    public List<Product> getAllProducts() {
        return repo.findAll();
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


        existingProduct.setImageName(imageFile.getName());
        existingProduct.setImageType(imageFile.getContentType());
        existingProduct.setImageData(imageFile.getBytes());

        return repo.save(existingProduct);
    }

    public void deleteProduct(int id) {
         repo.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword);
    }

}
