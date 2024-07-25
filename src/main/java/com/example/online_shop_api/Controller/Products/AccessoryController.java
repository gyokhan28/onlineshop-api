package com.example.online_shop_api.Controller.Products;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Accessory;
import com.example.online_shop_api.Service.Products.AccessoryService;
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
public class AccessoryController {
  private final AccessoryService accessoryService;

  @GetMapping("/show")
  public ResponseEntity<List<ProductResponseDto>> showAllAccessories() {
    return accessoryService.getAllAccessories();
  }

  @GetMapping("/{id}")
  ProductResponseDto getAccessoriesById(@PathVariable(name = "id") Long id){
    return accessoryService.getById(id);
  }

  @PostMapping("/add")
  public ResponseEntity<ProductResponseDto> addNewAccessory(
      @RequestBody @Valid ProductRequestDto requestDto) {
    return accessoryService.addAccessory(requestDto);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<ProductResponseDto> updateAccessory(
      @RequestBody @Valid ProductRequestDto requestDto, @PathVariable("id") Long id) {
    return accessoryService.updateAccessory(requestDto, id);
  }

  @DeleteMapping("/delete/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccessory(@PathVariable("id") Long id) {
    accessoryService.deleteAccessory(id);
  }
}
