package com.example.online_shop_api.Entity.Products;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product")
@SuperBuilder
@NoArgsConstructor
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  protected Long id;

  @Column(name = "product", insertable = false, updatable = false)
  protected String productType;

  protected String name;
  protected BigDecimal price;
  protected int quantity;

  @Column(name = "image_urls", columnDefinition = "TEXT")
  protected List<String> imageUrls;

  protected boolean isDeleted;

  public Product(String name, BigDecimal price, int quantity, List<String> imageUrls) {
    this.name = name;
    this.price = price;
    this.quantity = quantity;
    this.imageUrls = imageUrls;
    this.isDeleted = false;
  }

  public void setImageLocations(List<String> imageUrls) {
    // Set the image to null if not set in the frontend so that it doesn't show up an empty/missing
    // picture
    if (imageUrls.isEmpty()) {
      imageUrls = null;
    }
    this.imageUrls = imageUrls;
  }

  public void addImageUrl(String imageUrl) {
    if (!imageUrl.isBlank()) {
      this.imageUrls.add(imageUrl);
    }
  }
}
