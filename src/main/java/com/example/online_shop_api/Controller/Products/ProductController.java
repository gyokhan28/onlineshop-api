package com.example.online_shop_api.Controller.Products;

import com.example.online_shop_api.Dto.Request.ProductCreationRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Service.Products.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("/show")
  public ResponseEntity<List<ProductResponseDto>> getAll() {
    return productService.getAllAccessories();
  }

  @GetMapping("show/{id}")
  ProductResponseDto getProductById(@PathVariable(name = "id") Long id) {
    return productService.getById(id);
  }

  @PostMapping("/add")
  public ResponseEntity<ProductResponseDto> create(
          @RequestBody @Valid ProductCreationRequestDto requestDto) {
    return productService.create(requestDto);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<ProductResponseDto> update(
      @RequestBody @Valid ProductCreationRequestDto requestDto, @PathVariable("id") Long id) {
    return productService.update(requestDto, id);
  }

  @DeleteMapping("/delete/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccessory(@PathVariable("id") Long id) {
    productService.deleteAccessory(id);
  }
}
