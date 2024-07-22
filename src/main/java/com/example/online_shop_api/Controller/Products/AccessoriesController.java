package com.example.online_shop_api.Controller.Products;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Service.Products.AccessoriesService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/accessories")
@RequiredArgsConstructor
public class AccessoriesController {
  private final AccessoriesService accessoriesService;

  @GetMapping("/show")
  public ResponseEntity<List<ProductResponseDto>> showAllAccessories() {
    return accessoriesService.getAllAccessories();
  }

  @PostMapping("/add")
  public ResponseEntity<ProductResponseDto> addNewAccessories(
      @RequestBody @Valid ProductRequestDto requestDto) {
    return accessoriesService.addAccessories(requestDto);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<ProductResponseDto> updateAccessories(
      @RequestBody @Valid ProductRequestDto requestDto, @PathVariable("id") Long id) {
    return accessoriesService.updateAccessories(requestDto, id);
  }

  @DeleteMapping("delete/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccessories(@PathVariable("id") Long id) {
    accessoriesService.deleteAccessories(id);
  }
}
