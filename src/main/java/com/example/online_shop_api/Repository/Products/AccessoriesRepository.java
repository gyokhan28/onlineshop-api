package com.example.online_shop_api.Repository.Products;

import com.example.online_shop_api.Entity.Products.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessoriesRepository extends JpaRepository<Accessory, Long> {}

