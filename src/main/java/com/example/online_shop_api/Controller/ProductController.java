package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/add")
    public ResponseEntity<?> addNewProduct(@RequestParam("productType") String productType) {
        return productService.addNewProduct(productType);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getProduct(@RequestParam("productId") Long id) throws Exception {
        return productService.getProduct(id);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProducts() throws Exception {
        return productService.getAllProducts();
    }
}
