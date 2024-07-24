package com.example.online_shop_api.Mapper;

import com.example.online_shop_api.Dto.Response.ProductResponseDto;
import com.example.online_shop_api.Entity.Products.Product;
import com.example.online_shop_api.Service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final MinioService minioService;

    public ProductResponseDto toDto (Product product) throws Exception {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setQuantity(product.getQuantity());
        productResponseDto.setImageUrls(minioService.listFilesInDirectoryFullPath(product.getId().toString()));
        return productResponseDto;
    }

    public List<ProductResponseDto> toDtoList(List<Product> products) throws Exception {
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        for (Product p: products) {
            productResponseDtoList.add(toDto(p));
        }
        return productResponseDtoList;
    }
}
