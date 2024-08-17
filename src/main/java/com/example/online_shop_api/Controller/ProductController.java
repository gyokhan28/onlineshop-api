package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Request.UpdateProductRequestDto;
import com.example.online_shop_api.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    //TODO - add * for required fields?
    @GetMapping("/add")
    public ResponseEntity<?> getProductAttributes(@RequestParam("productType") String productType) throws ClassNotFoundException {
        return productService.getProductAttributes(productType);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewProduct(@RequestParam("productType") String productType,
                                           @RequestBody ProductRequestDto productRequestDto) {
        return productService.addNewProduct(productType, productRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) throws Exception {
        return productService.getProduct(id);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return productService.getAllProducts();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid UpdateProductRequestDto updateProductRequestDto, @PathVariable("id") Long id) {
        return productService.updateProduct(updateProductRequestDto, id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }
}
