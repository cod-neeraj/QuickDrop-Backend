package com.example.Product.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCard {
    private String orderId;
    private String userName;
    private String uerId;
    private List<ProductDetailsForRedis> productDetailsForRedis;
}
