package com.example.Product.Model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchAbleObject {
   private String id;
   private String name;
   private String type;
   private String brand;
   private String category;
   private Double rating;
   private Integer quantity;
   private String imageUrl;
   private Double matchPercentage;

}
