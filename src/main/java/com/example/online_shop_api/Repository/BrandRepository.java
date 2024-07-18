package com.example.online_shop_api.Repository;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
}
