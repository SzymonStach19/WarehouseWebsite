package org.example.magazyn.service;

import org.example.magazyn.dto.ProductDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.User;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getProductsByUser(User user);

    Product addProduct(ProductDto productDto, User currentUser) throws IOException;

    Product getProductById(Long id);

    void deleteProduct(Long id);

    Product updateProduct(Long id, ProductDto productDto) throws IOException;

    List<Product> findByZoneIsNull();

}