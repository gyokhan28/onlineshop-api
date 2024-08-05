package com.example.online_shop_api.Entity.Products;

import com.example.online_shop_api.Entity.ProductHelpers.Brand;
import com.example.online_shop_api.Entity.ProductHelpers.Color;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accessory extends Product {

  @ManyToOne
  @JoinColumn(name = "color_id")
  private Color color;

  @ManyToOne
  @JoinColumn(name = "brand_id")
  private Brand brand;
}
