package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LowStockDTO {
    private String productId;
    private String defaultImageUrl;
    private String name;
    private String brand;
    private Integer quantity;
}
