package com.example.online_shop_api.Service.Products;

import com.example.online_shop_api.Dto.Request.AddProductRequest;
import com.example.online_shop_api.Dto.Request.ProductRequestDto;
import com.example.online_shop_api.Repository.BrandRepository;
import com.example.online_shop_api.Repository.ColorRepository;
import com.example.online_shop_api.Repository.MaterialRepository;
import com.example.online_shop_api.Static.ProductCategory;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final BrandRepository brandRepository;

    public ResponseEntity<?> addNewProduct(String productType) {
        if (!isValidProductType(productType)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product type not found");
        }

        AddProductRequest request = new AddProductRequest();
        ProductRequestDto productRequestDto = new ProductRequestDto();

        request.setProductRequestDto(productRequestDto);
        request.setProductType(productType);
        addAttributesDependingOnProductType(productType, request);

        return ResponseEntity.ok(request);
    }

    private boolean isValidProductType(String productType) {
        return Arrays.stream(ProductCategory.values())
                .map(Enum::name)
                .anyMatch(name -> name.equalsIgnoreCase(productType.toUpperCase()));
    }

    private void addAttributesDependingOnProductType(String productType, AddProductRequest response) {
        if (productType.equalsIgnoreCase("Sanitary") || productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Decoration") || productType.equalsIgnoreCase("Others")) {
            response.setMaterials(materialRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Accessories")) {
            response.setColors(colorRepository.findAll());
            response.setBrands(brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Decoration")) {
            response.setBrands(brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Others")) {
            response.setColors(colorRepository.findAll());
        }
    }
}
