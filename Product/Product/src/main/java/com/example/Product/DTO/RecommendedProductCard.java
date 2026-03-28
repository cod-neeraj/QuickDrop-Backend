package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendedProductCard {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private String defaultImageUrl;
}
