package com.example.online_shop_api.Service.Products;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Accessories;
import com.example.online_shop_api.Exceptions.AccessoriesNotFoundException;
import com.example.online_shop_api.Exceptions.BrandNotExistException;
import com.example.online_shop_api.Exceptions.ColorNotExistException;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Repository.Products.AccessoriesRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccessoriesService {
  private final AccessoriesRepository accessoriesRepository;
  private final BrandRepository brandRepository;
  private final ColorRepository colorRepository;
  private final ModelMapper modelMapper;

  public ResponseEntity<List<ProductResponseDto>> getAllAccessories() {
    List<Accessories> accessoriesList = accessoriesRepository.findAll();
    List<ProductResponseDto> responseList =
        accessoriesList.stream()
            .map(accessory -> modelMapper.map(accessory, ProductResponseDto.class))
            .toList();
    return ResponseEntity.ok(responseList);
  }

  public ResponseEntity<ProductResponseDto> addAccessories(ProductRequestDto request) {
    Accessories accessories = modelMapper.map(request, Accessories.class);

    checkBrandAndColorExist(accessories);

    accessoriesRepository.save(accessories);
    return ResponseEntity.ok(modelMapper.map(accessories, ProductResponseDto.class));
  }

  public ResponseEntity<ProductResponseDto> updateAccessories(
      ProductRequestDto request, Long accessoriesId) {

    Accessories existingAccessories =
        accessoriesRepository
            .findById(accessoriesId)
            .orElseThrow(() -> new AccessoriesNotFoundException(accessoriesId));

    modelMapper.map(request, existingAccessories);
    checkBrandAndColorExist(existingAccessories);

    Accessories updatedAccessories = accessoriesRepository.save(existingAccessories);

    ProductResponseDto responseDto = modelMapper.map(updatedAccessories, ProductResponseDto.class);
    return ResponseEntity.ok(responseDto);
  }

  public void deleteAccessories(Long id) {
    Accessories accessories =
        accessoriesRepository.findById(id).orElseThrow(() -> new AccessoriesNotFoundException(id));
    accessoriesRepository.delete(accessories);
  }

  private void checkBrandAndColorExist(Accessories accessories) {
    boolean brandExists = brandRepository.existsById(accessories.getBrand().getId());
    boolean colorExists = colorRepository.existsById(accessories.getColor().getId());

    if (!brandExists) {
      throw new BrandNotExistException(accessories.getBrand().getId());
    }

    if (!colorExists) {
      throw new ColorNotExistException(accessories.getColor().getId());
    }
  }
}
