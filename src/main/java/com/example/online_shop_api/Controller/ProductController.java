package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/add")
    public ResponseEntity<?> addNewProduct(@RequestParam("productType") String productType){
        return productService.addNewProduct(productType);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewProduct(@RequestParam("productType") String productType,
                                           @RequestBody ProductRequestDto productRequestDto) {
        return productService.addNewProduct(productType, productRequestDto);
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
