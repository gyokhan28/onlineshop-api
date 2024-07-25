package com.example.online_shop_api.Controller;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Service.Products.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand")
public class BrandController {
  private final BrandService brandService;

  @GetMapping("/getAllBrand")
  public List<Brand> getAllBrand() {
    return brandService.getAllBrand();
  }

  @GetMapping("/getAllColors")
  public List<Color> getAllColors() {
    return brandService.getAllColors();
  }
}
