package org.example.magazyn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.magazyn.dto.ProductDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.User;
import org.example.magazyn.repository.UserRepository;
import org.example.magazyn.service.ProductService;
import org.example.magazyn.service.ZoneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final ZoneService zoneService; 

    @GetMapping("/products")
    public String getProducts(Model model, Principal principal) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "addProduct";
    }

    @PostMapping("/products/add")
    public String addProduct(
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            Principal principal
    ) throws IOException {
        User currentUser = userRepository.findByEmail(principal.getName());
        productService.addProduct(productDto, currentUser);
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        ProductDto productDto = new ProductDto();

        productDto.setName(product.getName());
        productDto.setCategory(product.getCategory());
        productDto.setDescription(product.getDescription());
        productDto.setPurchasePrice(product.getPurchasePrice());
        productDto.setPrice(product.getPrice());
        productDto.setWeight(product.getWeight());
        productDto.setBrand(product.getBrand());
        productDto.setQuantity(product.getQuantity());

        model.addAttribute("productDto", productDto);
        model.addAttribute("productId", id);
        return "editProduct";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductDto productDto
    ) throws IOException {
        productService.updateProduct(id, productDto);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product.getZone() != null) {
            zoneService.removeProductFromZone(id, product.getZone().getId());
        }
        productService.deleteProduct(id);
        return "redirect:/products";
    }


}