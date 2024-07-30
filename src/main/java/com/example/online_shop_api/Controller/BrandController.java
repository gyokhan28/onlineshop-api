package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Service.Products.ProductHelpersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand")
public class BrandController {
  private final ProductHelpersService productHelpersService;

  @GetMapping("/getAllBrand")
  public List<Brand> getAllBrand() {
    return productHelpersService.getAllBrand();
  }

  @GetMapping("/getAllColors")
  public List<Color> getAllColors() {
    return productHelpersService.getAllColors();
  }
}
