package com.example.Product.DTO;

import kotlin.BuilderInference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailsForRedis {
    private String productId;
    private Long shopkeeperId;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String attribute;
    private Double price;
    private Long quantity;
}
