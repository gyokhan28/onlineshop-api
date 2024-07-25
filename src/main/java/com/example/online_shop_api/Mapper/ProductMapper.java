package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.*;
import com.example.online_shop_api.Service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final MinioService minioService;

    public ProductResponseDto toDto(Product product) throws Exception {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setQuantity(product.getQuantity());
        productResponseDto.setImageUrls(minioService.listFilesInDirectoryFullPath(product.getId().toString()));

        // Add additional fields for each category
        if (product instanceof Accessories productCategory) {
            productResponseDto.setColor(productCategory.getColor());
            productResponseDto.setBrand(productCategory.getBrand());
        } else if (product instanceof Decoration productCategory) {
            productResponseDto.setMaterial(productCategory.getMaterial());
            productResponseDto.setBrand(productCategory.getBrand());
        } else if (product instanceof Drink drink) {
            productResponseDto.setBestBefore(drink.getBestBefore());
        } else if (product instanceof Food productCategory) {
            productResponseDto.setExpiryDate(productCategory.getExpiryDate());
        } else if (product instanceof Others productCategory) {
            productResponseDto.setMaterial(productCategory.getMaterial());
            productResponseDto.setColor(productCategory.getColor());
        } else if (product instanceof Railing productCategory) {
            productResponseDto.setMaterial(productCategory.getMaterial());
            productResponseDto.setColor(productCategory.getColor());
            productResponseDto.setBrand(productCategory.getBrand());
            productResponseDto.setNonSlip(productCategory.isNonSlip());
            productResponseDto.setOutdoor(productCategory.isOutdoor());
        } else if (product instanceof Sanitary productCategory) {
            productResponseDto.setMaterial(productCategory.getMaterial());
            productResponseDto.setBiodegradable(productCategory.isBiodegradable());
            productResponseDto.setReusable(productCategory.isReusable());
        }

        return productResponseDto;
    }

    public List<ProductResponseDto> toDtoList(List<Product> products) throws Exception {
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        for (Product p : products) {
            productResponseDtoList.add(toDto(p));
        }
        return productResponseDtoList;
    }
}
