package com.example.online_shop_api.Service.Products;

import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Accessory;
import com.example.online_shop_api.Exceptions.AccessoriesNotFoundException;
import com.example.online_shop_api.Exceptions.BrandNotExistException;
import com.example.online_shop_api.Exceptions.ColorNotExistException;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Repository.Products.AccessoriesRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessoryService {
  private final AccessoriesRepository accessoriesRepository;
  private final BrandRepository brandRepository;
  private final ColorRepository colorRepository;
  private final ModelMapper modelMapper;

  public ResponseEntity<List<ProductResponseDto>> getAllAccessories() {
    List<Accessory> accessoriesList = accessoriesRepository.findAll();
    return ResponseEntity.ok(
        accessoriesList.stream()
            .map(accessory -> modelMapper.map(accessory, ProductResponseDto.class))
            .toList());
  }
//  public ProductResponseDto getById(Long id) {
//    return  modelMapper.map(accessoriesRepository.findById(id),ProductResponseDto.class).orElseThrow(() -> new AccessoriesNotFoundException(id));
//
//  }

  public ResponseEntity<ProductResponseDto> addAccessory(ProductRequestDto request) {
    Accessory accessories = modelMapper.map(request, Accessory.class);

    checkBrandAndColorExist(accessories);
    accessories.setImageUrls(List.of(request.getImageLocation()));

    accessoriesRepository.save(accessories);
    return ResponseEntity.ok(modelMapper.map(accessories, ProductResponseDto.class));
  }

  public ResponseEntity<ProductResponseDto> updateAccessory(
      ProductRequestDto request, Long accessoriesId) {

    Accessory existingAccessories =
        accessoriesRepository
            .findById(accessoriesId)
            .orElseThrow(() -> new AccessoriesNotFoundException(accessoriesId));

    modelMapper.map(request, existingAccessories);
    checkBrandAndColorExist(existingAccessories);
    existingAccessories.setImageUrls(List.of(request.getImageLocation()));

    Accessory updatedAccessories = accessoriesRepository.save(existingAccessories);

    ProductResponseDto responseDto = modelMapper.map(updatedAccessories, ProductResponseDto.class);
    return ResponseEntity.ok(responseDto);
  }

  public void deleteAccessory(Long id) {
    Accessory accessories =
        accessoriesRepository.findById(id).orElseThrow(() -> new AccessoriesNotFoundException(id));
    accessoriesRepository.delete(accessories);
  }

  private void checkBrandAndColorExist(Accessory accessories) {
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
