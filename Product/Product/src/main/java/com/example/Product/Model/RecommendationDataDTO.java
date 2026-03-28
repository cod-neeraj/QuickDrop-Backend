package com.example.Product.Model;

import com.example.Product.Model.ProductData.ProductCategory;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationDataDTO {

    private String productId;
    private String name;
    private String type;
    private String brand;
    private ProductCategory productCategory;
    private Double ratings;
    private Integer quantity;
    private String defaultImageUrl;
}
