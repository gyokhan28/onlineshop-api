package com.example.online_shop_api.Service.Products;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import com.example.online_shop_api.Entity.ProductHelpers.Material;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductHelpersService {
  private final BrandRepository brandRepository;
  private final ColorRepository colorRepository;
  private final MaterialRepository materialRepository;

  public List<Brand> getAllBrand() {
    return brandRepository.findAll();
  }

  public List<Color> getAllColors() {
    return colorRepository.findAll();
  }

  public List<Material> getAllMaterials() {
    return materialRepository.findAll();
  }
}
